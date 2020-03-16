package com.mallivora.fabric.service;

import static com.mallivora.fabric.service.config.ChannelConfig.*;
import com.google.protobuf.ByteString;
import com.mallivora.fabric.service.cauthds.CauthdslUtil;
import com.mallivora.fabric.service.cauthds.PolicyParser;
import com.mallivora.fabric.service.config.AnchorPeer;
import com.mallivora.fabric.service.config.Application;
import com.mallivora.fabric.service.config.ChannelConfig;
import com.mallivora.fabric.service.config.Consortium;
import com.mallivora.fabric.service.config.Orderer;
import com.mallivora.fabric.service.config.Organization;
import com.mallivora.fabric.service.config.Policy;
import com.mallivora.fabric.service.config.Profile;
import com.mallivora.fabric.service.config.StandardConfigValue;
import com.mallivora.fabric.service.config.msp.MSPUtil;
import com.mallivora.fabric.service.config.policies.PoliciesUtil;
import com.mallivora.fabric.service.config.policies.StandardConfigPolicy;
import com.mallivora.fabric.service.configtxlator.Update;
import com.mallivora.fabric.service.utils.DateUtils;
import com.mallivora.fabric.service.utils.TxUtils;

import org.apache.commons.lang3.StringUtils;
import org.hyperledger.fabric.protos.common.Configtx;
import org.hyperledger.fabric.protos.common.Policies;
import org.hyperledger.fabric.protos.msp.MspConfigPackage;
import org.hyperledger.fabric.protos.peer.Configuration;
import org.hyperledger.fabric.protos.common.Common;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Encoder {

    private static final String ORDERER_ADMINS_POLICY_NAME = "/Channel/Orderer/Admins";
    private static final String BLOCK_VALIDATION_POLICY_KEY = "BlockValidation";
    private static final String AdminRoleAdminPrincipal = "Role.ADMIN";
    // ConsensusTypeSolo identifies the solo consensus implementation.
    private static final String CONSENSUS_TYPE_SOLO = "solo";
    // ConsensusTypeKafka identifies the Kafka-based consensus implementation.
    private static final String CONSENSUS_TYPE_KAFKA = "kafka";
    private static final String CONSENSUS_TYPE_RAFT = "etcdraft";
    // BlockValidationPolicyKey TODO
    private static final String Block_Validation_Policy_Key = "BlockValidation";
    // OrdererAdminsPolicy is the absolute path to the orderer admins policy
    private static final String ORDERER_ADMINS_POLICY = "/Channel/Orderer/Admins";
    // SignaturePolicyType is the 'Type' string for signature policies
    private static final String SIGNATURE_POLICY_TYPE = "Signature";
    // ImplicitMetaPolicyType is the 'Type' string for implicit meta policies
    private static final String IMPLICIT_META_POLICY_TYPE = "ImplicitMeta";

    private static final int msgVersion = 0;
    private static final int epoch = 0;

    public static void main(String[] args) throws IOException {
        Profile profile = Demo.newOutputChannelProfile();
        Common.Envelope configtx = makeChannelCreationTransaction("mychannel", profile);
        Files.write(Paths.get("E:\\channelTest.tx"), configtx.toByteArray(), StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }

    public static Common.Envelope makeChannelCreationTransaction(String channelId, Profile profile) {
        Configtx.ConfigGroup template = defaultConfigTemplate(profile);
        return makeChannelCreationTransactionFromTemplate(channelId, profile, template);
    }

    private static Common.Envelope makeChannelCreationTransactionFromTemplate(String channelId, Profile conf,
        Configtx.ConfigGroup template) {
        Configtx.ConfigUpdate newChannelConfigUpdate = newChannelCreateConfigUpdate(channelId, conf, template);
        Configtx.ConfigUpdateEnvelope newConfigUpdateEnv =
            Configtx.ConfigUpdateEnvelope.newBuilder().setConfigUpdate(newChannelConfigUpdate.toByteString()).build();
        return TxUtils.createSignedEnvelope(Common.HeaderType.CONFIG_UPDATE_VALUE, channelId, newConfigUpdateEnv,
            msgVersion, epoch);
    }

    public static Configtx.ConfigGroup defaultConfigTemplate(Profile conf) {
        Configtx.ConfigGroup.Builder channelGroup = newChannelGroup(conf).toBuilder();
        Configtx.ConfigGroup configGroup = channelGroup.getGroupsMap().get(Application.APPLICATION_GROUP_KEY);
        channelGroup.putGroups(Application.APPLICATION_GROUP_KEY,
            configGroup.toBuilder().clearValues().clearPolicies().build());
        return channelGroup.build();
    }

    public static Configtx.ConfigUpdate newChannelCreateConfigUpdate(String channelId, Profile conf,
        Configtx.ConfigGroup templateConfig) {

        if (null == conf.getApplication()) {
            throw new RuntimeException("cannot define a new channel with no Application section");
        }
        if (StringUtils.isBlank(conf.getConsortium())) {
            throw new RuntimeException("cannot define a new channel with no Consortium value");
        }
        Configtx.ConfigGroup newChannelGroup = newChannelGroup(conf);
        Configtx.ConfigUpdate.Builder compute =
            Update.compute(Configtx.Config.newBuilder().setChannelGroup(templateConfig).build(),
                Configtx.Config.newBuilder().setChannelGroup(newChannelGroup).build());
        compute.setChannelId(channelId);
        Configtx.ConfigGroup.Builder readSet = compute.getReadSet().toBuilder();
        readSet.putValues(ConsortiumKey, Configtx.ConfigValue.newBuilder().setVersion(0).build());
        Configtx.ConfigGroup.Builder writeSet = compute.getWriteSet().toBuilder();
        writeSet.putValues(ConsortiumKey,
            Configtx.ConfigValue.newBuilder().setVersion(0)
                .setValue(org.hyperledger.fabric.protos.common.Configuration.Consortium.newBuilder()
                    .setName(conf.getConsortium()).build().toByteString())
                .build());
        return compute.setReadSet(readSet).setWriteSet(writeSet).build();
    }

    public static Configtx.ConfigGroup newChannelGroup(Profile conf) {
        Configtx.ConfigGroup.Builder channelGroup = Configtx.ConfigGroup.newBuilder();
        Map<String, Policy> policies = conf.getPolicies();
        if (policies.size() == 0) {
            addImplicitMetaPolicyDefaults(channelGroup);
        } else {
            addPolicies(channelGroup, conf.getPolicies(), AdminsPolicyKey);
        }
        addValue(channelGroup, hashingAlgorithmValue(), AdminsPolicyKey);
        addValue(channelGroup, blockDataHashingStructureValue(), AdminsPolicyKey);

        if (null != conf.getOrderer() && conf.getOrderer().getAddresses().length > 0) {
            addValue(channelGroup, ordererAddressesValue(conf.getOrderer().getAddresses()), ORDERER_ADMINS_POLICY_NAME);
        }

        if (StringUtils.isNotBlank(conf.getConsortium())) {
            addValue(channelGroup, consortiumValue(conf.getConsortium()), AdminsPolicyKey);
        }

        if (conf.getCapabilities().size() > 0) {
            addValue(channelGroup, capabilitiesValue(conf.getCapabilities()), AdminsPolicyKey);
        }

        if (null != conf.getOrderer()) {
            channelGroup.putGroups(Orderer.ORDERER_GROUP_KEY, newOrdererGroup(conf.getOrderer()));
        }

        if (null != conf.getApplication()) {
            channelGroup.putGroups(Application.APPLICATION_GROUP_KEY, newApplicationGroup(conf.getApplication()));
        }

        if (null != conf.getConsortiums()) {
            channelGroup.putGroups(Consortium.CONSORTIUM_GROUP_KEY, newConsortiumsGroup(conf.getConsortiums()));
        }
        channelGroup.setModPolicy(AdminsPolicyKey);
        return channelGroup.build();
    }

    public static Configtx.ConfigGroup newOrdererGroup(Orderer conf) {
        Configtx.ConfigGroup.Builder ordererGroup = Configtx.ConfigGroup.newBuilder();
        if (conf.getPolicies().size() == 0) {
            addImplicitMetaPolicyDefaults(ordererGroup);
        } else {
            addPolicies(ordererGroup, conf.getPolicies(), AdminsPolicyKey);
        }
        Configtx.ConfigPolicy metaAnyPolicy = Configtx.ConfigPolicy.newBuilder()
            .setPolicy(PoliciesUtil.implicitMetaAnyPolicy(WritersPolicyKey).getValue()).setModPolicy(AdminsPolicyKey)
            .build();
        ordererGroup.putPolicies(BLOCK_VALIDATION_POLICY_KEY, metaAnyPolicy);
        addValue(
            ordererGroup, batchSizeValue(conf.getBatchSize().getMaxMessageCount(),
                conf.getBatchSize().getAbsoluteMaxBytes(), conf.getBatchSize().getPreferredMaxBytes()),
            AdminsPolicyKey);
        addValue(ordererGroup, batchTimeoutValue(DateUtils.dateTo(conf.getBatchTimeout())), AdminsPolicyKey);
        addValue(ordererGroup, channelRestrictionsValue(conf.getMaxChannels()), AdminsPolicyKey);

        if (conf.getPolicies().size() > 0) {
            addValue(ordererGroup, capabilitiesValue(conf.getCapabilities()), AdminsPolicyKey);
        }

        switch (conf.getOrdererType()) {
            case CONSENSUS_TYPE_SOLO:
            case CONSENSUS_TYPE_RAFT:
                break;
            case CONSENSUS_TYPE_KAFKA:
                addValue(ordererGroup, kafkaBrokersValue(conf.getKafka().getBrokers()), AdminsPolicyKey);
                break;
            default:
                throw new RuntimeException(String.format("unknown orderer type: %s", conf.getOrdererType()));
        }
        addValue(ordererGroup, consensusTypeValue(conf.getOrdererType(), conf.getEtcdRaft()), AdminsPolicyKey);

        Organization[] organizations = conf.getOrganizations();
        for (Organization organization : organizations) {
            ordererGroup.putGroups(organization.getName(), newOrdererOrgGroup(organization));
        }

        ordererGroup.setModPolicy(AdminsPolicyKey);
        return ordererGroup.build();
    }

    public static Configtx.ConfigGroup newOrdererOrgGroup(Organization conf) {
        MspConfigPackage.MSPConfig mspConfig =
            MSPUtil.getVerifyingMspConfig(conf.getMSPDir(), conf.getId(), conf.getMSPType());
        Configtx.ConfigGroup.Builder ordererOrgGroup = Configtx.ConfigGroup.newBuilder();
        if (conf.getPolicies().size() == 0) {
            addSignaturePolicyDefaults(ordererOrgGroup, conf.getId(),
                (conf.getAdminPrincipal() != AdminRoleAdminPrincipal));
        } else {
            addPolicies(ordererOrgGroup, conf.getPolicies(), AdminsPolicyKey);
        }
        addValue(ordererOrgGroup, ChannelConfig.MSPValue(mspConfig), AdminsPolicyKey);
        ordererOrgGroup.setModPolicy(AdminsPolicyKey);
        if (conf.getOrdererEndpoints().size() > 0) {
            addValue(ordererOrgGroup, ChannelConfig.endpointValue(conf.getOrdererEndpoints()), AdminsPolicyKey);
        }
        return ordererOrgGroup.build();
    }

    private static Configtx.ConfigGroup newApplicationGroup(Application application) {
        Configtx.ConfigGroup.Builder applicationGroup = Configtx.ConfigGroup.newBuilder();
        if (application.getPolicies().size() == 0) {
            addImplicitMetaPolicyDefaults(applicationGroup);
        } else {
            addPolicies(applicationGroup, application.getPolicies(), AdminsPolicyKey);
        }

        if (application.getACLs().size() > 0) {
            addValue(applicationGroup, ChannelConfig.getACLValues(application.getACLs()), AdminsPolicyKey);
        }

        if (application.getCapabilities().size() > 0) {
            addValue(applicationGroup, ChannelConfig.capabilitiesValue(application.getCapabilities()), AdminsPolicyKey);
        }

        for (Organization org : application.getOrganizations()) {
            applicationGroup.putGroups(org.getName(), newAPplicationOrgGroup(org));
        }
        applicationGroup.setModPolicy(AdminsPolicyKey);
        return applicationGroup.build();
    }

    private static Configtx.ConfigGroup newAPplicationOrgGroup(Organization org) {
        Configtx.ConfigGroup.Builder applicationOrgGroup = Configtx.ConfigGroup.newBuilder();
        if (org.getPolicies().size() == 0) {
            addSignaturePolicyDefaults(applicationOrgGroup, org.getId(),
                (org.getAdminPrincipal() != AdminRoleAdminPrincipal));
        } else {
            addPolicies(applicationOrgGroup, org.getPolicies(), AdminsPolicyKey);
        }
        addValue(applicationOrgGroup,
            ChannelConfig.MSPValue(MSPUtil.getVerifyingMspConfig(org.getMSPDir(), org.getId(), org.getMSPType())),
            AdminsPolicyKey);
        List<Configuration.AnchorPeer> anchorPeerList = new ArrayList<>();
        for (AnchorPeer anchorPeer : org.getAnchorPeers()) {
            anchorPeerList.add(Configuration.AnchorPeer.newBuilder().setHost(anchorPeer.getHost())
                .setPort(anchorPeer.getPort()).build());
        }
        if (anchorPeerList.size() > 0) {
            addValue(applicationOrgGroup, ChannelConfig.getAnchorPeersValue(anchorPeerList), AdminsPolicyKey);
        }
        applicationOrgGroup.setModPolicy(AdminsPolicyKey);
        return applicationOrgGroup.build();
    }

    private static Configtx.ConfigGroup newConsortiumsGroup(Map<String, Consortium> consortiums) {
        Configtx.ConfigGroup.Builder consortiumGroup = Configtx.ConfigGroup.newBuilder();
        addPolicy(consortiumGroup,
            PoliciesUtil.signaturePolicy(AdminsPolicyKey, Policies.SignaturePolicyEnvelope.newBuilder().build()),
            ORDERER_ADMINS_POLICY_NAME);
        consortiums.forEach((consortiumName, consortium) -> {
            consortiumGroup.putGroups(consortiumName, newConsortiumGroup(consortium));
        });
        consortiumGroup.setModPolicy(ORDERER_ADMINS_POLICY_NAME);
        return consortiumGroup.build();
    }

    private static Configtx.ConfigGroup newConsortiumGroup(Consortium conf) {
        Configtx.ConfigGroup.Builder consortiumGroup = Configtx.ConfigGroup.newBuilder();
        for (Organization org : conf.getOrganizations()) {
            consortiumGroup.putGroups(org.getName(), newConsortiumOrgGroup(org));
        }
        addValue(consortiumGroup,
            getChannelCreationPolicyValue(PoliciesUtil.implicitMetaAllPolicy(AdminsPolicyKey).getValue()),
            ORDERER_ADMINS_POLICY_NAME);
        consortiumGroup.setModPolicy(ORDERER_ADMINS_POLICY_NAME);
        return consortiumGroup.build();
    }

    private static Configtx.ConfigGroup newConsortiumOrgGroup(Organization org) {
        Configtx.ConfigGroup.Builder consortiumOrgGroup = Configtx.ConfigGroup.newBuilder();
        if (org.getPolicies().size() == 0) {
            addSignaturePolicyDefaults(consortiumOrgGroup, org.getId(),
                (org.getAdminPrincipal() != AdminRoleAdminPrincipal));
        } else {
            addPolicies(consortiumOrgGroup, org.getPolicies(), AdminsPolicyKey);
        }
        addValue(consortiumOrgGroup,
            ChannelConfig.MSPValue(MSPUtil.getVerifyingMspConfig(org.getMSPDir(), org.getId(), org.getMSPType())),
            AdminsPolicyKey);

        consortiumOrgGroup.setModPolicy(AdminsPolicyKey);
        return consortiumOrgGroup.build();
    }

    public static void addPolicies(Configtx.ConfigGroup.Builder configGroupBuilder, Map<String, Policy> policies,
        String modPolicy) {
        for (@SuppressWarnings("rawtypes")
        Map.Entry entry : policies.entrySet()) {
            String policyName = (String)entry.getKey();
            Policy policy = (Policy)entry.getValue();
            switch (policy.getType()) {
                case IMPLICIT_META_POLICY_TYPE:
                    Policies.ImplicitMetaPolicy implicitMetaPolicy = implicitMetaFromString(policy.getRule());
                    Configtx.ConfigPolicy configPolicy = newConfigPolicy(modPolicy,
                        Policies.Policy.PolicyType.IMPLICIT_META_VALUE, implicitMetaPolicy.toByteString());
                    configGroupBuilder.putPolicies(policyName, configPolicy);
                    break;
                case SIGNATURE_POLICY_TYPE:
                    Policies.SignaturePolicyEnvelope sp = PolicyParser.fromString(policy.getRule());
                    configGroupBuilder.putPolicies(policyName,
                        newConfigPolicy(modPolicy, Policies.Policy.PolicyType.SIGNATURE_VALUE, sp.toByteString()));
                    break;
                default:
                    break;
            }
        }
    }

    public static void addPolicy(Configtx.ConfigGroup.Builder builder, StandardConfigPolicy policy, String modPolicy) {
        builder.putPolicies(policy.getKey(),
            Configtx.ConfigPolicy.newBuilder().setPolicy(policy.getValue()).setModPolicy(modPolicy).build());
    }

    public static void addValue(Configtx.ConfigGroup.Builder builder, StandardConfigValue value, String modPolicy) {
        Configtx.ConfigValue configValue =
            Configtx.ConfigValue.newBuilder().setValue(value.getValue().toByteString()).setModPolicy(modPolicy).build();
        builder.putValues(value.getKey(), configValue);
    }

    public static Configtx.ConfigPolicy newConfigPolicy(String modPolicy, int type, ByteString value) {

        return Configtx.ConfigPolicy.newBuilder().setModPolicy(modPolicy).setPolicy(newPolicy(type, value)).build();
    }

    public static Policies.Policy newPolicy(int type, ByteString value) {
        return Policies.Policy.newBuilder().setType(type).setValue(value).build();
    }

    public static Policies.ImplicitMetaPolicy implicitMetaFromString(String input) {
        String[] args = input.split(" ");
        int ruleValue = 0;
        switch (args[0]) {
            case "ANY":
                ruleValue = Policies.ImplicitMetaPolicy.Rule.ANY_VALUE;
                break;
            case "ALL":
                ruleValue = Policies.ImplicitMetaPolicy.Rule.ALL_VALUE;
                break;
            case "MAJORITY":
                ruleValue = Policies.ImplicitMetaPolicy.Rule.MAJORITY_VALUE;
                break;
            default:
                throw new RuntimeException(
                    String.format("unknown rule type '%s', expected ALL, ANY, or MAJORITY", args[0]));
        }

        return Policies.ImplicitMetaPolicy.newBuilder().setRuleValue(ruleValue).setSubPolicy(args[1]).build();
    }

    public static void addImplicitMetaPolicyDefaults(Configtx.ConfigGroup.Builder builder) {
        addPolicy(builder, PoliciesUtil.implicitMetaMajorityPolicy(AdminsPolicyKey), AdminsPolicyKey);
        addPolicy(builder, PoliciesUtil.implicitMetaAnyPolicy(ReadersPolicyKey), AdminsPolicyKey);
        addPolicy(builder, PoliciesUtil.implicitMetaAllPolicy(WritersPolicyKey), AdminsPolicyKey);
    }

    public static void addSignaturePolicyDefaults(Configtx.ConfigGroup.Builder cg, String mspId, boolean devMode) {
        if (devMode) {
            addPolicy(cg, PoliciesUtil.signaturePolicy(AdminsPolicyKey, CauthdslUtil.signedByMspMember(mspId)),
                AdminsPolicyKey);
        } else {
            addPolicy(cg, PoliciesUtil.signaturePolicy(AdminsPolicyKey, CauthdslUtil.signedByMspAdmin(mspId)),
                AdminsPolicyKey);
        }
        addPolicy(cg, PoliciesUtil.signaturePolicy(ReadersPolicyKey, CauthdslUtil.signedByMspMember(mspId)),
            AdminsPolicyKey);
        addPolicy(cg, PoliciesUtil.signaturePolicy(WritersPolicyKey, CauthdslUtil.signedByMspMember(mspId)),
            AdminsPolicyKey);
    }

}
