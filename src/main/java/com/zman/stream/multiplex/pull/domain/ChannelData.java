package com.zman.stream.multiplex.pull.domain;

import java.util.Arrays;

public class ChannelData {

    private String channelId;

    private String resourceId;

    private int type;

    private byte[] payload;

    public ChannelData(String channelId, String resourceId, int type, byte[] payload) {
        this.channelId = channelId;
        this.resourceId = resourceId;
        this.type = type;
        this.payload = payload;
    }

    public String getChannelId() {
        return channelId;
    }


    public String getResourceId() {
        return resourceId;
    }


    public byte[] getPayload() {
        return payload;
    }


    public int getType() {
        return type;
    }


}
