package com.mallivora.fabric.service.configtxlator;

import org.hyperledger.fabric.protos.common.Configtx;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Update {

    public static Configtx.ConfigUpdate.Builder compute(Configtx.Config original, Configtx.Config updated) {

        if (null == original.getChannelGroup()) {
            throw new RuntimeException("no channel group included for original config");
        }
        if (null == updated.getChannelGroup()) {
            throw new RuntimeException("no channel group included for updated config");
        }

        GroupUpdate update = computeGroupUpdate(original.getChannelGroup(), updated.getChannelGroup());
        if (!update.isUpdatedMembers()) {
            throw  new RuntimeException("no differences detected between original and updated config");
        }

        return update.getConfigUpdate();
    }

    private static GroupUpdate computeGroupUpdate(Configtx.ConfigGroup original, Configtx.ConfigGroup updated) {
        GroupUpdate groupUpdate = new GroupUpdate();

        PoliciesMap policiesMap = computePoliciesMapUpdate(original.getPoliciesMap(), updated.getPoliciesMap());
        ValuesMap valuesMap = computeValuesMapUpdate(original.getValuesMap(), updated.getValuesMap());
        GroupsMap groupsMap = computeGroupsMapUpdate(original.getGroupsMap(), updated.getGroupsMap());
        Configtx.ConfigUpdate.Builder builder = Configtx.ConfigUpdate.newBuilder();
        if (!(policiesMap.isUpdatedMembers() || valuesMap.isUpdatedMembers() || groupsMap.isUpdatedMembers()
            || !original.getModPolicy().equals(updated.getModPolicy()))) {
            if (policiesMap.getReadSet().size() == 0 && policiesMap.getWriteSet().size() == 0
                && valuesMap.getWriteSet().size() == 0 && valuesMap.getReadSet().size() == 0
                && groupsMap.getWriteSet().size() == 0) {
                groupUpdate.setConfigUpdate(
                    builder.setReadSet(Configtx.ConfigGroup.newBuilder().setVersion(original.getVersion()).build())
                        .setWriteSet(Configtx.ConfigGroup.newBuilder().setVersion(original.getVersion()).build()));
                groupUpdate.setUpdatedMembers(false);
                return groupUpdate;
            }
            groupUpdate.setConfigUpdate(builder
                .setReadSet(Configtx.ConfigGroup.newBuilder().setVersion(original.getVersion())
                    .putAllPolicies(policiesMap.getReadSet()).putAllValues(valuesMap.getReadSet())
                    .putAllGroups(groupsMap.getReadSet()).build())
                .setWriteSet(Configtx.ConfigGroup.newBuilder().setVersion(original.getVersion())
                    .putAllPolicies(policiesMap.getWriteSet()).putAllGroups(groupsMap.getWriteSet())
                    .putAllValues(valuesMap.getWriteSet()).build()));
            groupUpdate.setUpdatedMembers(true);
            return groupUpdate;
        }
        Map<String, Configtx.ConfigPolicy> readSetPolicies = policiesMap.getReadSet();
        Map<String, Configtx.ConfigPolicy> writeSetPolicies = policiesMap.getWriteSet();
        policiesMap.getSameSet().forEach((k, samePolicy) -> {
            readSetPolicies.put(k, samePolicy);
            writeSetPolicies.put(k, samePolicy);
        });

        Map<String, Configtx.ConfigValue> readSetValues = valuesMap.getReadSet();
        Map<String, Configtx.ConfigValue> writeSetValues = valuesMap.getWriteSet();
        valuesMap.getSameSet().forEach((k, sameValue) -> {
            readSetValues.put(k, sameValue);
            writeSetValues.put(k, sameValue);
        });

        Map<String, Configtx.ConfigGroup> readSetGroups = groupsMap.getReadSet();
        Map<String, Configtx.ConfigGroup> writeSetGroups = groupsMap.getWriteSet();
        groupsMap.getSameSet().forEach((k, sameGroup) -> {
            readSetGroups.put(k, sameGroup);
            writeSetGroups.put(k, sameGroup);
        });
        groupUpdate.setConfigUpdate(builder
            .setReadSet(Configtx.ConfigGroup.newBuilder().setVersion(original.getVersion())
                .putAllPolicies(readSetPolicies).putAllValues(readSetValues).putAllGroups(readSetGroups).build())
            .setWriteSet(Configtx.ConfigGroup.newBuilder().setVersion(original.getVersion() + 1)
                .putAllPolicies(writeSetPolicies).putAllGroups(writeSetGroups).putAllValues(writeSetValues)
                .setModPolicy(updated.getModPolicy()).build()));
        groupUpdate.setUpdatedMembers(true);
        return groupUpdate;
    }

    private static GroupsMap computeGroupsMapUpdate(Map<String, Configtx.ConfigGroup> original,
        Map<String, Configtx.ConfigGroup> updated) {
        Map<String, Configtx.ConfigGroup> readSet = new HashMap<String, Configtx.ConfigGroup>();
        Map<String, Configtx.ConfigGroup> writeSet = new HashMap<String, Configtx.ConfigGroup>();
        Map<String, Configtx.ConfigGroup> sameSet = new HashMap<String, Configtx.ConfigGroup>();
        AtomicBoolean updatedMembers = new AtomicBoolean(false);
        GroupsMap groupsMap = new GroupsMap();

        original.forEach((groupName, originalGroup) -> {
            Configtx.ConfigGroup updatedGroup = updated.get(groupName);
            if (null == updated.get(groupName)) {
                updatedMembers.set(true);
                return;
            }
            GroupUpdate groupUpdate = computeGroupUpdate(originalGroup, updatedGroup);
            if (!groupUpdate.isUpdatedMembers()) {
                sameSet.put(groupName, groupUpdate.getConfigUpdate().getReadSet());
                return;
            }
            readSet.put(groupName, groupUpdate.getConfigUpdate().getReadSet());
            writeSet.put(groupName, groupUpdate.getConfigUpdate().getWriteSet());
        });

        updated.forEach((groupName, updatedGroup) -> {
            if (null == original.get(groupName)) {
                updatedMembers.set(true);
                GroupUpdate groupUpdate = computeGroupUpdate(Configtx.ConfigGroup.newBuilder().build(), updatedGroup);
                Configtx.ConfigGroup groupWriteSet = groupUpdate.getConfigUpdate().getWriteSet();
                writeSet.put(groupName,
                    Configtx.ConfigGroup.newBuilder().setVersion(0L).setModPolicy(updatedGroup.getModPolicy())
                        .putAllPolicies(groupWriteSet.getPoliciesMap()).putAllValues(groupWriteSet.getValuesMap())
                        .putAllGroups(groupWriteSet.getGroupsMap()).build());
            }
        });
        groupsMap.setUpdatedMembers(updatedMembers.get());
        groupsMap.setReadSet(readSet);
        groupsMap.setWriteSet(writeSet);
        groupsMap.setSameSet(sameSet);
        return groupsMap;
    }

    private static ValuesMap computeValuesMapUpdate(Map<String, Configtx.ConfigValue> original,
        Map<String, Configtx.ConfigValue> updated) {
        ValuesMap valuesMap = new ValuesMap();
        Map<String, Configtx.ConfigValue> readSet = new HashMap<String, Configtx.ConfigValue>();
        Map<String, Configtx.ConfigValue> writeSet = new HashMap<String, Configtx.ConfigValue>();
        Map<String, Configtx.ConfigValue> sameSet = new HashMap<String, Configtx.ConfigValue>();
        AtomicBoolean updatedMembers = new AtomicBoolean(false);

        original.forEach((valueName, originalValue) -> {
            Configtx.ConfigValue updatedValue = updated.get(valueName);
            if (null == updatedValue) {
                updatedMembers.set(true);
                return;
            }
            if (originalValue.getModPolicy().equals(updatedValue.getModPolicy())
                && updatedValue.getValue().equals(originalValue.getValue())) {
                sameSet.put(valueName,
                    Configtx.ConfigValue.newBuilder().setVersion(originalValue.getVersion()).build());
                return;
            }
            writeSet.put(valueName, Configtx.ConfigValue.newBuilder().setVersion(originalValue.getVersion() + 1)
                .setModPolicy(updatedValue.getModPolicy()).setValue(updatedValue.getValue()).build());
        });

        updated.forEach((valueName, updatedValue) -> {

            if (null == original.get(valueName)) {
                updatedMembers.set(true);
                writeSet.put(valueName, Configtx.ConfigValue.newBuilder().setVersion(0L)
                    .setModPolicy(updatedValue.getModPolicy()).setValue(updatedValue.getValue()).build());
            }
        });
        valuesMap.setUpdatedMembers(updatedMembers.get());
        valuesMap.setSameSet(sameSet);
        valuesMap.setReadSet(readSet);
        valuesMap.setWriteSet(writeSet);
        return valuesMap;
    }

    private static PoliciesMap computePoliciesMapUpdate(Map<String, Configtx.ConfigPolicy> original,
        Map<String, Configtx.ConfigPolicy> updated) {
        Map<String, Configtx.ConfigPolicy> readSet = new HashMap<String, Configtx.ConfigPolicy>();
        Map<String, Configtx.ConfigPolicy> writeSet = new HashMap<String, Configtx.ConfigPolicy>();
        Map<String, Configtx.ConfigPolicy> sameSet = new HashMap<String, Configtx.ConfigPolicy>();
        PoliciesMap policiesMap = new PoliciesMap();
        AtomicBoolean updatedMembers = new AtomicBoolean(false);

        original.forEach((policyName, originalPolicy) -> {
            Configtx.ConfigPolicy updatedPolicy = updated.get(policyName);
            if (null == updatedPolicy) {
                updatedMembers.set(true);
                return;
            }
            if (originalPolicy.getModPolicy() == updatedPolicy.getModPolicy() && originalPolicy.equals(updatedPolicy)) {
                sameSet.put(policyName,
                    Configtx.ConfigPolicy.newBuilder().setVersion(originalPolicy.getVersion()).build());
                return;
            }
            writeSet.put(policyName, Configtx.ConfigPolicy.newBuilder().setVersion(originalPolicy.getVersion() + 1)
                .setModPolicy(updatedPolicy.getModPolicy()).setPolicy(updatedPolicy.getPolicy()).build());
        });

        updated.forEach((policyName, updatedPolicy) -> {
            if (null == original.get(policyName)) {
                updatedMembers.set(true);
                writeSet.put(policyName, Configtx.ConfigPolicy.newBuilder().setVersion(0L)
                    .setModPolicy(updatedPolicy.getModPolicy()).setPolicy(updatedPolicy.getPolicy()).build());
            }
        });
        policiesMap.setUpdatedMembers(updatedMembers.get());
        policiesMap.setReadSet(readSet);
        policiesMap.setSameSet(sameSet);
        policiesMap.setWriteSet(writeSet);
        return policiesMap;
    }

    static class ValuesMap {
        private Map<String, Configtx.ConfigValue> readSet;
        private Map<String, Configtx.ConfigValue> writeSet;
        private Map<String, Configtx.ConfigValue> sameSet;
        private boolean updatedMembers;

        public Map<String, Configtx.ConfigValue> getReadSet() {
            return readSet;
        }

        public void setReadSet(Map<String, Configtx.ConfigValue> readSet) {
            this.readSet = readSet;
        }

        public Map<String, Configtx.ConfigValue> getWriteSet() {
            return writeSet;
        }

        public void setWriteSet(Map<String, Configtx.ConfigValue> writeSet) {
            this.writeSet = writeSet;
        }

        public Map<String, Configtx.ConfigValue> getSameSet() {
            return sameSet;
        }

        public void setSameSet(Map<String, Configtx.ConfigValue> sameSet) {
            this.sameSet = sameSet;
        }

        public boolean isUpdatedMembers() {
            return updatedMembers;
        }

        public void setUpdatedMembers(boolean updatedMembers) {
            this.updatedMembers = updatedMembers;
        }
    }

    static class PoliciesMap {
        private Map<String, Configtx.ConfigPolicy> readSet;
        private Map<String, Configtx.ConfigPolicy> writeSet;
        private Map<String, Configtx.ConfigPolicy> sameSet;
        private boolean updatedMembers;

        public Map<String, Configtx.ConfigPolicy> getReadSet() {
            return readSet;
        }

        public void setReadSet(Map<String, Configtx.ConfigPolicy> readSet) {
            this.readSet = readSet;
        }

        public Map<String, Configtx.ConfigPolicy> getWriteSet() {
            return writeSet;
        }

        public void setWriteSet(Map<String, Configtx.ConfigPolicy> writeSet) {
            this.writeSet = writeSet;
        }

        public Map<String, Configtx.ConfigPolicy> getSameSet() {
            return sameSet;
        }

        public void setSameSet(Map<String, Configtx.ConfigPolicy> sameSet) {
            this.sameSet = sameSet;
        }

        public boolean isUpdatedMembers() {
            return updatedMembers;
        }

        public void setUpdatedMembers(boolean updatedMembers) {
            this.updatedMembers = updatedMembers;
        }
    }

    static class GroupsMap {
        private Map<String, Configtx.ConfigGroup> readSet;
        private Map<String, Configtx.ConfigGroup> writeSet;
        private Map<String, Configtx.ConfigGroup> sameSet;
        private boolean updatedMembers;

        public Map<String, Configtx.ConfigGroup> getReadSet() {
            return readSet;
        }

        public void setReadSet(Map<String, Configtx.ConfigGroup> readSet) {
            this.readSet = readSet;
        }

        public Map<String, Configtx.ConfigGroup> getWriteSet() {
            return writeSet;
        }

        public void setWriteSet(Map<String, Configtx.ConfigGroup> writeSet) {
            this.writeSet = writeSet;
        }

        public Map<String, Configtx.ConfigGroup> getSameSet() {
            return sameSet;
        }

        public void setSameSet(Map<String, Configtx.ConfigGroup> sameSet) {
            this.sameSet = sameSet;
        }

        public boolean isUpdatedMembers() {
            return updatedMembers;
        }

        public void setUpdatedMembers(boolean updatedMembers) {
            this.updatedMembers = updatedMembers;
        }
    }

    static class GroupUpdate {
        private Configtx.ConfigUpdate.Builder configUpdate;
        private boolean updatedMembers;

        public Configtx.ConfigUpdate.Builder getConfigUpdate() {
            return configUpdate;
        }

        public boolean isUpdatedMembers() {
            return updatedMembers;
        }

        public void setConfigUpdate(Configtx.ConfigUpdate.Builder configUpdate) {
            this.configUpdate = configUpdate;
        }

        public void setUpdatedMembers(boolean updatedMembers) {
            this.updatedMembers = updatedMembers;
        }
    }

}
