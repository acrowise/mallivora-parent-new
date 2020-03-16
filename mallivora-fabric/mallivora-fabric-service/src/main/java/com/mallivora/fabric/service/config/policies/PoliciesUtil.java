package com.mallivora.fabric.service.config.policies;
import org.hyperledger.fabric.protos.common.Policies;
public class PoliciesUtil {

    public static StandardConfigPolicy implicitMetaAnyPolicy(String policyName) {
        return new StandardConfigPolicy(){
            {
                setKey(policyName);
                setValue(makeImplicitMetaPolicy(policyName,Policies.ImplicitMetaPolicy.Rule.ANY_VALUE));
            }
        };
    }

    public static StandardConfigPolicy implicitMetaAllPolicy(String policyName) {
        return new StandardConfigPolicy(){
            {
                setKey(policyName);
                setValue(makeImplicitMetaPolicy(policyName,Policies.ImplicitMetaPolicy.Rule.ALL_VALUE));
            }
        };
    }


    public static StandardConfigPolicy implicitMetaMajorityPolicy(String policyName) {
        return new StandardConfigPolicy(){
            {
                setKey(policyName);
                setValue(makeImplicitMetaPolicy(policyName,Policies.ImplicitMetaPolicy.Rule.MAJORITY_VALUE));
            }
        };
    }

    public static Policies.Policy makeImplicitMetaPolicy(String subPolicyName,int rule) {
        Policies.ImplicitMetaPolicy metaPolicy = Policies.ImplicitMetaPolicy.newBuilder().setRule(Policies.ImplicitMetaPolicy.Rule.forNumber(rule)).setSubPolicy(subPolicyName).build();
        Policies.Policy policy = Policies.Policy.newBuilder().setType(Policies.Policy.PolicyType.IMPLICIT_META_VALUE).setValue(metaPolicy.toByteString()).build();
        return policy;
    }

    public static StandardConfigPolicy signaturePolicy(String policyName, Policies.SignaturePolicyEnvelope sigPolicy) {
        return new StandardConfigPolicy() {
            {
                setKey(policyName);
                setValue(Policies.Policy.newBuilder().setType(Policies.Policy.PolicyType.SIGNATURE_VALUE).setValue(sigPolicy.toByteString()).build());
            }
        };
    }
}
