package com.mallivora.fabric.service.cauthds;

import org.hyperledger.fabric.protos.common.Policies;
import org.hyperledger.fabric.protos.common.MspPrincipal;

import java.util.Arrays;

public class CauthdslUtil {

    public static Policies.SignaturePolicyEnvelope signedByMspMember(String mspId) {
        return signedByFabricEntity(mspId, MspPrincipal.MSPRole.MSPRoleType.MEMBER_VALUE);
    }

    private static Policies.SignaturePolicyEnvelope signedByFabricEntity(String mspId, int memberValue) {
        MspPrincipal.MSPRole mspRole =
            MspPrincipal.MSPRole.newBuilder().setRoleValue(memberValue).setMspIdentifier(mspId).build();
        MspPrincipal.MSPPrincipal mspPrincipal = MspPrincipal.MSPPrincipal.newBuilder()
            .setPrincipalClassificationValue(MspPrincipal.MSPPrincipal.Classification.ROLE_VALUE)
            .setPrincipal(mspRole.toByteString()).build();
        Policies.SignaturePolicy[] policies =
            new Policies.SignaturePolicy[] {Policies.SignaturePolicy.newBuilder().setSignedBy(0).build()};
        return Policies.SignaturePolicyEnvelope.newBuilder().setVersion(0).setRule(nOutOf(1, policies))
            .addIdentities(mspPrincipal).build();
    }

    public static Policies.SignaturePolicy nOutOf(int n, Policies.SignaturePolicy[] policies) {
        Policies.SignaturePolicy.NOutOf nOutOf =
            Policies.SignaturePolicy.NOutOf.newBuilder().setN(n).addAllRules(Arrays.asList(policies)).build();
        Policies.SignaturePolicy signaturePolicy = Policies.SignaturePolicy.newBuilder().setNOutOf(nOutOf).build();
        return signaturePolicy;
    }

    public static Policies.SignaturePolicy signedBy(int index) {
        return Policies.SignaturePolicy.newBuilder().setSignedBy(index).build();
    }

    public static Policies.SignaturePolicyEnvelope signedByMspAdmin(String mspId) {
        MspPrincipal.MSPRole mspRole =
                MspPrincipal.MSPRole.newBuilder().setRoleValue(MspPrincipal.MSPRole.MSPRoleType.ADMIN_VALUE).setMspIdentifier(mspId).build();
        Policies.SignaturePolicy[] policies =
                new Policies.SignaturePolicy[] {Policies.SignaturePolicy.newBuilder().setSignedBy(0).build()};
        return Policies.SignaturePolicyEnvelope.newBuilder().setVersion(0).setRule(nOutOf(1, policies))
                .addIdentities(MspPrincipal.MSPPrincipal.newBuilder()
                        .setPrincipalClassificationValue(MspPrincipal.MSPPrincipal.Classification.ROLE_VALUE)
                        .setPrincipal(mspRole.toByteString()).build()).build();
    }
}
