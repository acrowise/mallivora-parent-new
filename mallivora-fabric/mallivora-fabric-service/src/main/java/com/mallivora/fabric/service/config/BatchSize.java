package com.mallivora.fabric.service.config;

public class BatchSize {

    private int MaxMessageCount;
    private int AbsoluteMaxBytes;
    private int PreferredMaxBytes;

    public int getMaxMessageCount() {
        return MaxMessageCount;
    }

    public void setMaxMessageCount(int maxMessageCount) {
        MaxMessageCount = maxMessageCount;
    }

    public int getAbsoluteMaxBytes() {
        return AbsoluteMaxBytes;
    }

    public void setAbsoluteMaxBytes(int absoluteMaxBytes) {
        AbsoluteMaxBytes = absoluteMaxBytes;
    }

    public int getPreferredMaxBytes() {
        return PreferredMaxBytes;
    }

    public void setPreferredMaxBytes(int preferredMaxBytes) {
        PreferredMaxBytes = preferredMaxBytes;
    }
}
