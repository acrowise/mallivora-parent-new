package com.mallivora.fabric.service.cauthds;

import org.apache.commons.lang3.ArrayUtils;
import org.hyperledger.fabric.protos.common.Policies;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hyperledger.fabric.protos.common.Policies.*;

import com.mallivora.fabric.service.functionalInter.ExpressionFunction;
import com.mallivora.fabric.service.govaluate.EvaluableExpression;
import com.mallivora.fabric.service.govaluate.EvaluationStage;

import org.hyperledger.fabric.protos.common.MspPrincipal;

public class PolicyParser {

    private static final String GateAnd = "And";
    private static final String GateOr = "Or";
    private static final String GateOutOf = "OutOf";
    private static final String RoleAdmin = "admin";
    private static final String RoleMember = "member";
    private static final String RoleClient = "client";
    private static final String RolePeer = "peer";
    private static final String RoleOrderer = "orderer";

    private static final String PATTERN = "^([\\p{Alnum}.-]+)(.)(admin|member|client|peer|orderer)$";

    public static Policies.SignaturePolicyEnvelope fromString(String policy) {
        Map<String, ExpressionFunction> functions = new HashMap<String, ExpressionFunction>() {
            {
                put(GateAnd, PolicyParser::and);
                put(GateAnd.toLowerCase(), PolicyParser::and);
                put(GateAnd.toUpperCase(), PolicyParser::and);
                put(GateOr, PolicyParser::or);
                put(GateOr.toUpperCase(), PolicyParser::or);
                put(GateOr.toLowerCase(), PolicyParser::or);
                put(GateOutOf, PolicyParser::outof);
                put(GateOutOf.toUpperCase(), PolicyParser::outof);
                put(GateOutOf.toLowerCase(), PolicyParser::outof);
            }
        };
        EvaluableExpression intermediate = EvaluableExpression.newEvaluableExpressionWithFunctions(policy, functions);
        Object intermediateRes = intermediate.evaluate(new HashMap<>());
        String resStr = "";
        try {
            resStr = String.valueOf(intermediateRes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        EvaluableExpression exp = EvaluableExpression.newEvaluableExpressionWithFunctions(resStr, new HashMap<String, ExpressionFunction>() {
            {
                put("outof", PolicyParser::firstPass);
            }
        });
        Object res = exp.evaluate(new HashMap<>());
        try {
            resStr = String.valueOf(res);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Context context = Context.newContext();
        HashMap<String, Object> parameters = new HashMap<String, Object>() {
            {
                put("ID", context);
            }
        };
        exp = EvaluableExpression.newEvaluableExpressionWithFunctions(resStr, new HashMap<String, ExpressionFunction>() {
            {
                put("outof", PolicyParser::secondPass);
            }
        });
        res = exp.evaluate(parameters);
        Policies.SignaturePolicy rule = (Policies.SignaturePolicy) res;

        Policies.SignaturePolicyEnvelope signaturePolicyEnvelope = Policies.SignaturePolicyEnvelope.newBuilder().addAllIdentities(Arrays.asList(context.getPrincipals())).setVersion(0).setRule(rule).build();
        return signaturePolicyEnvelope;
    }

    private static Object getObject(String toret, Object[] args) {
        Object[] objects = Arrays.copyOfRange(args, 1, args.length);
        for (Object arg : objects) {
            toret += ",";
            if (EvaluationStage.isString(arg)) {
                if (Pattern.matches(PATTERN, (String) arg)) {
                    toret += "'" + arg + "'";
                } else {
                    toret += arg;
                }
            } else {
                throw new RuntimeException(String.format("Unexpected type %s", arg.getClass().getTypeName()));
            }
        }

        return toret + ")";
    }

    public static Object outof(Object... args) {
        String toret = "outof(";
        if (args.length < 2) {
            throw new RuntimeException("Expected at least two arguments to NOutOf");
        }
        Object arg0 = args[0];
        if (EvaluationStage.isFloat64(arg0)) {
            toret += arg0;
        } else if ("java.lang.Integer".equals(arg0.getClass().getTypeName())) {
            toret += arg0;
        } else if (EvaluationStage.isString(arg0)) {
            toret += arg0;
        } else {
            throw new RuntimeException("Unexpected type ");
        }

        return getObject(toret, args);
    }

    public static Object and(Object... args) {
        return outof(ArrayUtils.addAll(new Object[]{args.length}, args));
    }

    public static Object or(Object... args) {
        return outof(ArrayUtils.addAll(new Object[]{1}, args));
    }

    public static Object firstPass(Object... args) {
        String toret = "outof(ID";
        for (Object arg : args) {
            toret += ", ";
            if (EvaluationStage.isString(arg)) {
                if (Pattern.matches(PATTERN, (String) arg)) {
                    toret += "'" + arg + "'";
                } else {
                    toret += (String) arg;
                }
            } else if (EvaluationStage.isFloat64(arg)) {
                Float f = (Float) arg;
                toret += f.intValue();
            }
        }
        return toret + ")";
    }

    private static Object secondPass(Object... args) {
        String toret = "outof(";
        if (args.length < 3) {
            throw new RuntimeException(String.format("At least 3 arguments expected, got %d", args.length));
        }
        Context ctx;
        Object arg0 = args[0];
        if (arg0 instanceof Context) {
            ctx = (Context) arg0;
        } else {
            throw new RuntimeException(String.format("Unrecognized type, expected the context, got %s", arg0.getClass().getTypeName()));
        }

        Object arg1 = args[1];
        int t;
        if (EvaluationStage.isFloat64(arg1)) {
            Float f = (Float) arg1;
            t = f.intValue();
        } else {
            throw new RuntimeException(String.format("Unrecognized type, expected a number, got %s", arg1.getClass().getTypeName()));
        }

        int n = args.length - 2;
        if (t < 0 || t > n + 1) {
            throw new RuntimeException(String.format("Invalid t-out-of-n predicate, t %d, n %d", t, n));
        }

        Policies.SignaturePolicy build = Policies.SignaturePolicy.newBuilder().build();
        SignaturePolicy[] policies = new SignaturePolicy[]{};
        Object[] objects = Arrays.copyOfRange(args, 2, args.length);
        for (Object principal : objects) {
            if (EvaluationStage.isString(principal)) {
                int r;
                Pattern compile = Pattern.compile(PATTERN);
                Matcher matcher = compile.matcher((String) principal);
                if (matcher.groupCount() != 3) {
                    throw new RuntimeException(String.format("Error parsing principal %s", principal));
                }
                /*MspPrincipal.MSPRole.MSPRoleType;*/
                MspPrincipal.MSPRole mspRole = null;
                if (matcher.find()) {
                    switch (matcher.group(3)) {
                        case RoleMember:
                            r = MspPrincipal.MSPRole.MSPRoleType.MEMBER_VALUE;
                            break;
                        case RoleAdmin:
                            r = MspPrincipal.MSPRole.MSPRoleType.ADMIN_VALUE;
                            break;
                        case RoleClient:
                            r = MspPrincipal.MSPRole.MSPRoleType.CLIENT_VALUE;
                            break;
                        case RolePeer:
                            r = MspPrincipal.MSPRole.MSPRoleType.PEER_VALUE;
                            break;
                        case RoleOrderer:
                            r = MspPrincipal.MSPRole.MSPRoleType.ORDERER_VALUE;
                            break;
                        default:
                            throw new RuntimeException(String.format("Error parsing role %s", t));
                    }
                    mspRole = MspPrincipal.MSPRole.newBuilder().setMspIdentifier(matcher.group(1)).setRole(MspPrincipal.MSPRole.MSPRoleType.forNumber(r)).build();
                }

                MspPrincipal.MSPPrincipal mspPrincipal = MspPrincipal.MSPPrincipal.newBuilder().
                                                                    setPrincipalClassification(MspPrincipal.MSPPrincipal.Classification.forNumber(MspPrincipal.MSPPrincipal.Classification.ROLE_VALUE)).
                                                                    setPrincipal(mspRole.toByteString()).build();

                ctx.setPrincipals(ArrayUtils.addAll(ctx.getPrincipals(),mspPrincipal));
                SignaturePolicy signaturePolicy = CauthdslUtil.signedBy(ctx.getIDNum());
                policies = ArrayUtils.add(policies,signaturePolicy);
                ctx.setIDNum(ctx.getIDNum() + 1);
            } else if (principal instanceof Policies.SignaturePolicy) {
                policies = ArrayUtils.add(policies, (SignaturePolicy)principal);
            } else {
                throw new RuntimeException(String.format("Unrecognized type, expected a principal or a policy, got %s", principal.getClass().getTypeName()));
            }
        }
        return CauthdslUtil.nOutOf(t,policies);
    }





    public static void main(String[] args) {
        Policies.SignaturePolicyEnvelope signaturePolicyEnvelope = fromString("OR('Org1MSP.admin', 'Org1MSP.peer', 'Org1MSP.client')");
        System.out.println("");
    }


}
