package com.zman.stream.multiplex.pull.impl;

import com.zman.pull.stream.IDuplex;
import com.zman.pull.stream.impl.DefaultDuplex;
import com.zman.pull.stream.impl.DefaultStreamBuffer;
import com.zman.stream.multiplex.pull.IChannel;
import com.zman.stream.multiplex.pull.IMultiplex;

import static com.zman.stream.multiplex.pull.enums.ChannelDataType.ChannelClose;
import static com.zman.stream.multiplex.pull.enums.ChannelDataType.NormalData;


public class DefaultChannel implements IChannel {


    private String channelId;

    private String resourceId;

    private IDuplex<byte[]> duplex;

    private IMultiplex multiplex;


    public DefaultChannel(String channelId, String resourceId, IMultiplex multiplex){

        this.channelId = channelId;
        this.resourceId = resourceId;

        this.multiplex = multiplex;

        duplex = new DefaultDuplex<>(new DefaultStreamBuffer<>(),
                this::onData, this::onClose, this::onException);
    }


    public void onData(byte[] data){
        multiplex.pushSource(this, NormalData.ordinal(), data);
    }
    public void onClose(){
        multiplex.pushSource(this, ChannelClose.ordinal(), null);
    }
    public void onException(Throwable throwable){
        multiplex.pushSource(this, ChannelClose.ordinal(), null);
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
     * There is only two channels(local & remote) with the unique resourceId in one multiplex.
     *
     * @return the unique resource id in all systems
     */
    @Override
    public String resourceId() {
        return resourceId;
    }

    /**
     * close duplex, and send close event to multiplex
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
