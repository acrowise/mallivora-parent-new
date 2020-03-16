package com.mallivora.fabric.service.govaluate;

import org.apache.commons.lang3.ArrayUtils;
import com.mallivora.fabric.service.functionalInter.EvaluationOperator;
import com.mallivora.fabric.service.functionalInter.ExpressionFunction;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import static com.mallivora.fabric.service.govaluate.EvaluableExpression.typeCheck;
import static com.mallivora.fabric.service.govaluate.OperatorSymbol.*;
import static com.mallivora.fabric.service.govaluate.TokenKind.*;


public class StagePlanner {

    private static Map<Integer,EvaluationOperator<Object,Object,Map<String,Object>,Object>> stageSymbolMap = new HashMap<Integer,EvaluationOperator<Object,Object,Map<String,Object>,Object>>(){
        {
            put(EQ,EvaluationStage::equalStage);
            put(NEQ,EvaluationStage::notEqualStage);
            put(GT,EvaluationStage::gtStage);
            put(LT,EvaluationStage::ltStage);
            put(GTE,EvaluationStage::gteStage);
            put(LTE,EvaluationStage::lteStage);
            put(REQ,EvaluationStage::regexStage);
            put(NREQ,EvaluationStage::notRegexStage);
            /*put(AND,EvaluationStage::andStage);
            put(OR,EvaluationStage::orStage);
            put(IN,EvaluationStage::inStage);
            put(BITWISE_OR,);
            put(BITWISE_AND,);
            put(BITWISE_XOR,);
            put(BITWISE_LSHIFT,);
            put(BITWISE_RSHIFT,);
            put(PLUS,);
            put(MINUS,);
            put(MULTIPLY,);
            put(DIVIDE,);
            put(MODULUS,);
            put(EXPONENT,);
            put(NEGATE,);
            put(INVERT,);
            put(BITWISE_NOT,);
            put(TERNARY_TRUE,);
            put(TERNARY_FALSE,);
            put(COALESCE,);
            put(SEPARATE,);*/
        }
    };

    private Function<TokenStream,EvaluationStage> planPrefix ;
    private Function<TokenStream,EvaluationStage> planExponential;
    private Function<TokenStream,EvaluationStage> planMultiplicative;
    private Function<TokenStream,EvaluationStage> planAdditive;
    private Function<TokenStream,EvaluationStage> planBitwise;
    private Function<TokenStream,EvaluationStage> planShift;
    private Function<TokenStream,EvaluationStage> planComparator;
    private Function<TokenStream,EvaluationStage> planLogicalAnd;
    private Function<TokenStream,EvaluationStage> planLogicalOr;
    private Function<TokenStream,EvaluationStage> planTernary;
    private Function<TokenStream,EvaluationStage> planSeparator;



    public Function<TokenStream, EvaluationStage> getPlanExponential() {
        return planExponential;
    }

    public Function<TokenStream, EvaluationStage> getPlanMultiplicative() {
        return planMultiplicative;
    }

    public Function<TokenStream, EvaluationStage> getPlanAdditive() {
        return planAdditive;
    }


    public Function<TokenStream, EvaluationStage> getPlanBitwise() {
        return planBitwise;
    }


    public Function<TokenStream, EvaluationStage> getPlanShift() {
        return planShift;
    }

    public Function<TokenStream, EvaluationStage> getPlanComparator() {
        return planComparator;
    }

    public Function<TokenStream, EvaluationStage> getPlanLogicalAnd() {
        return planLogicalAnd;
    }

    public Function<TokenStream, EvaluationStage> getPlanLogicalOr() {
        return planLogicalOr;
    }

    public Function<TokenStream, EvaluationStage> getPlanTernary() {
        return planTernary;
    }

    public Function<TokenStream, EvaluationStage> getPlanSeparator() {
        return planSeparator;
    }

    private StagePlanner(){
        this.planPrefix = makePrecedentFromPlanner(new PrecedencePlanner(){
            {
                setValidSymbols(prefixMap);
                setValidKinds(new int[]{PREFIX});
                setNextRight(StagePlanner::planFunction);
            }
        });
        this.planExponential = makePrecedentFromPlanner(new PrecedencePlanner(){
            {
                setValidSymbols(exponentialMap);
                setValidKinds(new int[]{MODIFIER});
                setNext(StagePlanner::planFunction);
            }
        });
        this.planMultiplicative = makePrecedentFromPlanner(new PrecedencePlanner(){
            {
                setValidSymbols(multiplicativeMap);
                setValidKinds(new int[]{MODIFIER});
                setNext(planExponential);
            }
        });
        this.planAdditive = makePrecedentFromPlanner(new PrecedencePlanner(){
            {
                setValidSymbols(additiveMap);
                setValidKinds(new int[]{MODIFIER});
                setNext(planMultiplicative);
            }
        });
        this.planShift = makePrecedentFromPlanner(new PrecedencePlanner(){
            {
                setValidSymbols(bitwiseShiftMap);
                setValidKinds(new int[]{MODIFIER});
                setNext(planAdditive);
            }
        });
        this.planBitwise = makePrecedentFromPlanner(new PrecedencePlanner(){
            {
                setValidSymbols(bitwiseMap);
                setValidKinds(new int[]{MODIFIER});
                setNext(planShift);
            }
        });
        this.planComparator = makePrecedentFromPlanner(new PrecedencePlanner(){
            {
                setValidSymbols(comparatorMap);
                setValidKinds(new int[]{COMPARATOR});
                setNext(planBitwise);
            }
        });
        this.planLogicalAnd = makePrecedentFromPlanner(new PrecedencePlanner(){
            {
                setValidSymbols(new HashMap<String,Integer>(){
                    {
                        put("&&",AND);
                    }
                });
                setValidKinds(new int[]{LOGICALOP});
                setNext(planComparator);
            }
        });
        this.planLogicalOr = makePrecedentFromPlanner(new PrecedencePlanner(){
            {
                setValidSymbols(new HashMap<String,Integer>(){
                    {
                        put("||",OR);
                    }
                });
                setValidKinds(new int[]{LOGICALOP});
                setNext(planLogicalAnd);
            }
        });
        planTernary = makePrecedentFromPlanner(new PrecedencePlanner(){
            {
                setValidSymbols(ternaryMap);
                setValidKinds(new int[]{TERNARY});
                setNext(planLogicalOr);
            }
        });
        planSeparator = makePrecedentFromPlanner(new PrecedencePlanner(){
            {
                setValidSymbols(separatorMap);
                setValidKinds(new int[]{SEPARATOR});
                setNext(planTernary);
            }
        });
    }

    public Function<TokenStream, EvaluationStage> getPlanPrefix() {
        return planPrefix;
    }

    public static EvaluationStage planStages(ExpressionToken[] tokens){
        TokenStream tokenStream = new TokenStream(tokens);
        EvaluationStage stage = planTokens(tokenStream);
        reorderStages(stage);
        return elideLiterals(stage);
    }

    static EvaluationStage planFunction(TokenStream stream){
        ExpressionToken token = stream.next();
        if (token.getKind() != FUNCTION){
            stream.rewind();
            return planValue(stream);
        }
        EvaluationStage finalRightStage = planValue(stream);
        ExpressionFunction function = (ExpressionFunction) token.getValue();
        return new EvaluationStage(){
            {
                setSymbol(FUNCTIONAL);
                setRightStage(finalRightStage);
                setOperator(makeFunctionStage(function));
                setTypeErrorFormat("Unable to run function '%v': %v");
            }
        };
    }

    static EvaluationStage planValue(TokenStream stream){
        EvaluationStage ret = null;
        ExpressionToken token = stream.next();
        EvaluationOperator operator;
        int symbol = 0;
        switch (token.getKind()){
            case CLAUSE:
                ret = planTokens(stream);
                stream.next();
                EvaluationStage newStage = new EvaluationStage();
                newStage.setRightStage(ret);
                newStage.setOperator(new EvaluationOperator() {
                    @Override
                    public Object apply(Object left, Object right, Object o3) {
                        return right;
                    }
                });
                newStage.setSymbol(NOOP);
                return newStage;
            case CLAUSE_CLOSE:
                stream.rewind();
                return null;
            case VARIABLE:
                operator = EvaluationStage.makeParameterStage(String.valueOf(token.getValue()));
                break;
            case NUMERIC:
            case STRING:
            case PATTERN:
            case BOOLEAN:
                symbol = LITERAL;
                operator = EvaluationStage.makeLiteralStage(token.getValue());
                break;
            case TIME:
                symbol = LITERAL;
                Date date = new Date(Long.parseLong(String.valueOf(token.getValue())));
                operator = EvaluationStage.makeLiteralStage(date.getTime() / 1000);
                break;
            case PREFIX:
                stream.rewind();
                return new StagePlanner().getPlanPrefix().apply(stream);
            default:
                throw new IllegalStateException("Unexpected value: " + token.getKind());
        }
        if (operator == null){
            return null;
        }
        EvaluationStage stage = new EvaluationStage();
        stage.setSymbol(symbol);
        stage.setOperator(operator);
        return stage;
    }

    static EvaluationStage elideLiterals(EvaluationStage root){
        if(null != root.getLeftStage()){
            root.setLeftStage(elideLiterals(root.getLeftStage()));
        }
        if(null != root.getRightStage()){
            root.setRightStage(elideLiterals(root.getRightStage()));
        }

        return elideStage(root);
    }

    private static EvaluationStage elideStage(EvaluationStage root) {
        Object leftValue, rightValue, result;
        if(null == root.getRightStage() ||
            root.getRightStage().getSymbol() != LITERAL ||
            null == root.getLeftStage() ||
            root.getLeftStage().getSymbol() != LITERAL){
            return root;
        }
        switch (root.getSymbol()) {
            case SEPARATE:
            case IN:
                return root;
        }

        leftValue = root.getLeftStage().getOperator().apply(null,null,null);
        rightValue = root.getRightStage().getOperator().apply(null,null,null);

        if(!typeCheck(root.getLeftTypeCheck(),leftValue,root.getSymbol(),root.getTypeErrorFormat())){
            return root;
        }
        if(!typeCheck(root.getRightTypeCheck(),rightValue,root.getSymbol(),root.getTypeErrorFormat())){
            return root;
        }

        if(null != root.getTypeCheck() && !root.getTypeCheck().apply(leftValue,rightValue)){
            return root;
        }
        result = root.getOperator().apply(leftValue, rightValue, null);
        return new EvaluationStage(){
            {
                setSymbol(LITERAL);
                setOperator(makeLiteralStage(result));
            }
        };
    }

    public static EvaluationStage planTokens(TokenStream tokenStream){
        if(!tokenStream.hashNext()){
            return null;
        }
        return planSeparator(tokenStream);
    }
    public static EvaluationStage planSeparator(TokenStream tokenStream){
        return new StagePlanner().getPlanSeparator().apply(tokenStream);
    }

    public static Function<TokenStream,EvaluationStage> makePrecedentFromPlanner(PrecedencePlanner planner){
        Function<TokenStream,EvaluationStage> generated = null;
        generated = new Function<TokenStream,EvaluationStage>(){
            @Override
            public EvaluationStage apply(TokenStream tokenStream) {
                return planPrecedenceLevel(tokenStream,
                        planner.getTypeErrorFormat(),
                        planner.getValidSymbols(),
                        planner.getValidKinds(),
                        planner.getNextRight(),
                        planner.next);
            }
        };
        planner.setNextRight(generated);
        if (null == planner.getNextRight()){
            generated= new Function<TokenStream,EvaluationStage>(){
                @Override
                public EvaluationStage apply(TokenStream tokenStream) {
                    EvaluationStage stage = planPrecedenceLevel(tokenStream,
                            planner.getTypeErrorFormat(),
                            planner.getValidSymbols(),
                            planner.getValidKinds(),
                            planner.getNextRight(),
                            planner.next);
                    return stage;
                }
            };
        }
        return generated;
    }

    public static EvaluationStage planPrecedenceLevel(TokenStream stream,
                                           String typeErrorFormat,
                                           Map<String, Integer> validSymbols,
                                           int[] validKinds,
                                           Function<TokenStream,EvaluationStage> rightPrecedent,
                                           Function<TokenStream,EvaluationStage> leftPrecedent) {
        EvaluationStage leftStage = null;
        EvaluationStage rightStage = null;
        ExpressionToken token;
        TypeChecks checks;
        int symbol = 0;
        if(null != leftPrecedent){
            leftStage = (EvaluationStage) leftPrecedent.apply(stream);
        }

        while(stream.hashNext()){
            token = stream.next();
            if(validKinds.length > 0 ) {

                boolean keyFound = false;

                for (int kind : validKinds) {
                    if (kind == token.getKind()){
                        keyFound = true;
                        break;
                    }
                }
                if(!keyFound){
                    break;
                }

            }

            if(null != validSymbols){
                if(!EvaluationStage.isString(token.getValue()) && !"java.lang.Character".equals(token.getValue().getClass().getTypeName())){
                    break;
                }
                try{
                    symbol = validSymbols.get(String.valueOf(token.getValue()));
                }catch (NullPointerException e){
                    break;
                }
            }
            
            if(null != rightPrecedent) {
                rightStage = (EvaluationStage) rightPrecedent.apply(stream);
            }

            checks =  findTypeChecks(symbol);
            EvaluationStage stage = new EvaluationStage();
            stage.setSymbol(symbol);
            stage.setLeftStage(leftStage);
            stage.setRightStage(rightStage);
            stage.setOperator(EvaluationStage::separatorStage);
            stage.setLeftTypeCheck(checks.getLeft());
            stage.setRightTypeCheck(checks.getRight());
            stage.setTypeCheck(checks.getCombined());
            stage.setTypeErrorFormat(typeErrorFormat);
            return stage;
        }
        stream.rewind();
        return leftStage;
    }

    public static TypeChecks findTypeChecks(int symbol){
        switch (symbol){
            case GT:
            case LT:
            case GTE:
            case LTE:
                return new TypeChecks(){
                    {
                        setCombined(EvaluationStage::comparatorTypeCheck);
                    }
                };
            case REQ:
            case NREQ:
                return new TypeChecks(){
                    {
                        setLeft(EvaluationStage::isString);
                        setRight(EvaluationStage::isRegexOrString);
                    }
                };
            case AND:
            case OR:
                return new TypeChecks(){
                    {
                        setLeft(EvaluationStage::isBool);
                        setRight(EvaluationStage::isBool);
                    }
                };
            case IN:
                return new TypeChecks(){
                    {
                        setRight(EvaluationStage::isArray);
                    }
                };
            case BITWISE_LSHIFT:
            case BITWISE_RSHIFT:
            case BITWISE_OR:
            case BITWISE_AND:
            case BITWISE_XOR:
                return new TypeChecks(){
                    {
                        setLeft(EvaluationStage::isFloat64);
                        setRight(EvaluationStage::isFloat64);
                    }
                };
            case PLUS:
                return new TypeChecks(){
                    {
                        setCombined(EvaluationStage::additionTypeCheck);
                    }
                };
            case MINUS:
            case MULTIPLY:
            case DIVIDE:
            case MODULUS:
            case EXPONENT:
                return new TypeChecks(){
                    {
                        setLeft(EvaluationStage::isFloat64);
                        setRight(EvaluationStage::isFloat64);
                    }
                };
            case NEGATE:
                return new TypeChecks(){
                    {
                        setRight(EvaluationStage::isFloat64);
                    }
                };
            case INVERT:
                return new TypeChecks(){
                    {
                        setRight(EvaluationStage::isBool);
                    }
                };
            case BITWISE_NOT:
                return new TypeChecks(){
                    {
                        setRight(EvaluationStage::isFloat64);
                    }
                };
            case TERNARY_TRUE:
                return new TypeChecks(){
                    {
                        setLeft(EvaluationStage::isBool);
                    }
                };
            default:
                return new TypeChecks();
        }
    }

    static void reorderStages(EvaluationStage rootStage){
        EvaluationStage[] identicalPrecedences = new EvaluationStage[]{};
        EvaluationStage currentStage;
        EvaluationStage nextStage;
        int currentPrecedence = 0;
        nextStage = rootStage;
        int precedence = findOperatorPrecedenceForSymbol(rootStage.getSymbol());
        while (null != nextStage) {
            currentStage = nextStage;
            nextStage = currentStage.getRightStage();
            if(null != currentStage.getLeftStage()) {
                reorderStages(currentStage.getLeftStage());
            }

            currentPrecedence = findOperatorPrecedenceForSymbol(currentStage.getSymbol());

            if(currentPrecedence == precedence) {
                ArrayUtils.contains(identicalPrecedences, currentStage);
                continue;
            }

            if(identicalPrecedences.length > 1) {
                mirrorStageSubtree(identicalPrecedences);
            }
        }
    }

    static void mirrorStageSubtree(EvaluationStage[] stages) {
        EvaluationStage rootStage,inverseStage,carryStage,frontStage;
        int length = stages.length;
        for (EvaluationStage stage : stages){
            frontStage = stage;
            carryStage = frontStage.getRightStage();
            frontStage.setRightStage(frontStage.getLeftStage());
            frontStage.setLeftStage(carryStage);
        }

        rootStage = stages[0];
        frontStage = stages[length - 1];
        carryStage = frontStage.getLeftStage();
        frontStage.setLeftStage(frontStage.getRightStage());
        frontStage.setRightStage(carryStage);

        for (int i = 0; i < (length - 2) / (2 + 1) ; i++){
            frontStage = stages[i + 1];
            inverseStage = stages[length -i - 1];
            carryStage = frontStage.getRightStage();
            frontStage.setRightStage(inverseStage.getRightStage());
            inverseStage.setRightStage(carryStage);
        }

        for (int i = 0; i < length/2; i++){
            frontStage = stages[i];
            inverseStage = stages[length - i - 1];
            frontStage.swapWith(inverseStage);
        }

    }


    public static EvaluationOperator test(Boolean b){
        return (EvaluationOperator<Object, Object, Object, Boolean>) (o, o2, o3) -> b;
    }

    class PrecedencePlanner {
        private Map<String,Integer> validSymbols;
        private int[] validKinds;
        private String typeErrorFormat;
        private Function<TokenStream,EvaluationStage> next;
        private Function<TokenStream,EvaluationStage> nextRight;

        public Map<String, Integer> getValidSymbols() {
            return validSymbols;
        }

        public void setValidSymbols(Map<String, Integer> validSymbols) {
            this.validSymbols = validSymbols;
        }

        public int[] getValidKinds() {
            return validKinds;
        }

        public void setValidKinds(int[] validKinds) {
            this.validKinds = validKinds;
        }

        public String getTypeErrorFormat() {
            return typeErrorFormat;
        }

        public void setTypeErrorFormat(String typeErrorFormat) {
            this.typeErrorFormat = typeErrorFormat;
        }

        public Function<TokenStream, EvaluationStage> getNext() {
            return next;
        }

        public void setNext(Function<TokenStream, EvaluationStage> next) {
            this.next = next;
        }

        public Function<TokenStream, EvaluationStage> getNextRight() {
            return nextRight;
        }

        public void setNextRight(Function<TokenStream, EvaluationStage> nextRight) {
            this.nextRight = nextRight;
        }
    }

}
