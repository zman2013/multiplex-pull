package com.zman.stream.multiplex.pull.impl;

import com.zman.pull.stream.IDuplex;
import com.zman.pull.stream.impl.DefaultDuplex;
import com.zman.pull.stream.impl.DefaultStreamBuffer;
import com.zman.stream.multiplex.pull.IChannel;
import com.zman.stream.multiplex.pull.IMultiplex;

import static com.zman.stream.multiplex.pull.enums.ChannelDataType.ChannelClose;
import static com.zman.stream.multiplex.pull.enums.ChannelDataType.NormalData;


public class DefaultChannel implements IChannel {

    private boolean localChannel;

    private String channelId;

    private String resourceId;

    private IDuplex<byte[]> duplex;

    private IMultiplex multiplex;


    public DefaultChannel(boolean localChannel, String channelId, String resourceId, IMultiplex multiplex){

        this.localChannel = localChannel;
        this.channelId = channelId;
        this.resourceId = resourceId;

        this.multiplex = multiplex;

        duplex = new DefaultDuplex<>(this::onData, this::onClose);
    }


    private boolean onData(byte[] data){
        multiplex.pushSource(this, NormalData.ordinal(), data);
        return false;
    }
    private void onClose(Throwable throwable){
        multiplex.pushSource(this, ChannelClose.ordinal(), new byte[0]);
    }

    /**
     * The local channel is created by local multiplex; the remote channel is created by remote multiplex.
     *
     * @return true: local channel, false: mirror channel of the remote channel
     */
    @Override
    public boolean isLocalChannel() {
        return localChannel;
    }

    /**
     * @return unique id for this channel
     */
    @Override
    public String id() {
        return channelId;
    }

    /**
     * Which resource this channel supports, for example: signalModel、bleModel、AccountModel, etc.
     * <p>
     * There is only two channels(local and remote) with the unique resourceId in one multiplex.
     *
     * @return the unique resource id in all systems
     */
    @Override
    public String resourceId() {
        return resourceId;
    }

    /**
     * close duplex
     */
    @Override
    public void close() {
        duplex.close();
        multiplex.pushSource(this, ChannelClose.ordinal(), null);
    }

    @Override
    public IDuplex<byte[]> duplex() {
        return duplex;
    }


}
