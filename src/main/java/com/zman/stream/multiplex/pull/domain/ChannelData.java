package com.zman.stream.multiplex.pull.domain;


public class ChannelData {

    private byte localChannel;

    /**
     * 十位字符串：ascii
     */
    private String channelId;

    /**
     * 十位字符串：ascii
     */
    private String resourceId;

    private int type;

    private byte[] payload;

    public ChannelData(boolean isLocalChannel, String channelId, String resourceId, int type, byte[] payload) {
        this.localChannel = (byte) (isLocalChannel?1:0);
        this.channelId = channelId;
        this.resourceId = resourceId;
        this.type = type;
        this.payload = payload;
    }

    public byte getLocalChannel() {
        return localChannel;
    }

    public boolean isLocalChannel() {
        return localChannel==1;
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
