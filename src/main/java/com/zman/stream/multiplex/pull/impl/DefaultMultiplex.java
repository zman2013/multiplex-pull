package com.zman.stream.multiplex.pull.impl;

import com.zman.pull.stream.IDuplex;
import com.zman.pull.stream.impl.DefaultDuplex;
import com.zman.stream.multiplex.pull.IChannel;
import com.zman.stream.multiplex.pull.IMultiplex;
import com.zman.stream.multiplex.pull.domain.ChannelData;
import com.zman.stream.multiplex.pull.enums.ChannelDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.zman.stream.multiplex.pull.enums.ChannelDataType.ChannelInit;


/**
 * The multiplex is paired between two nodes, one called local multiplex, the other called remote multiplex.
 * <p>
 * You can create a local channel from one multiplex, the paired remote multiplex will create a remote channel automatically.
 * <p>
 * The channel pair will be linked logically.
 * <p>
 * The multiplex will maintain a local channel list and a remote channel list.
 * The local channel is created by local multiplex, the remote channel is created by remote multiplex.
 * One local channel of local multiplex is paired with one remote channel of the remote multiplex.
 */
public class DefaultMultiplex implements IMultiplex {


    private int channelIdSeed = 1000_000_000;

    private Map<String, IChannel> localChannelMap = new HashMap<>();

    private Map<String, IChannel> remoteChannelMap = new HashMap<>();

    private IDuplex<ChannelData> duplex;

    private Consumer<IChannel> onAccept;

    private Consumer<Throwable> onClosed;


    public DefaultMultiplex() {
        duplex = new DefaultDuplex<>(this::onData, this::onDuplexClosed);
    }


    /**
     * create a local channel and trigger remote multiplex creating a remote channel.
     *
     * @return the local channel which is linked with the remote channel
     */
    @Override
    public IChannel createChannel(String resourceId) {
        IChannel channel = new DefaultChannel(true, String.valueOf(channelIdSeed++), resourceId, this);
        localChannelMap.put(channel.id(), channel);

        pushSource(channel, ChannelInit.ordinal(), new byte[0]);

        return channel;
    }

    @Override
    public IMultiplex onAccept(Consumer<IChannel> onAccept) {
        this.onAccept = onAccept;
        return this;
    }

    /**
     * The channel will invoke {@link #pushSource(IChannel, int, byte[])} to push data into the multiplex's source buffer.
     * <p>
     * If the multiplex's source buffer is not full, this function will return true, or else it return false.
     * <p>
     * When the multiplex's source buffer is full, the multiplex should store the channel reference.
     * And when it has more space to accept data, it should notify the failed channel to pushSource again.
     *
     * @param channel channel
     * @param data    data
     * @return true for success, false for failure
     */
    @Override
    public boolean pushSource(IChannel channel, int channelDataType, byte[] data) {

        ChannelData channelData = new ChannelData(channel.isLocalChannel(), channel.id(), channel.resourceId(), channelDataType, data);
        duplex.source().push(channelData);

        return true;
    }


    /**
     * destroy the multiplex.
     * <p>
     * The function will trigger the multiplex to `send` close signal to all local and remote channels.
     * <p>
     * The `send` means read from source with parameter end=true.
     */
    @Override
    public void destroy(Throwable throwable) {
        localChannelMap.values().forEach(channel -> channel.duplex().close());
        remoteChannelMap.values().forEach(channel -> channel.duplex().close());
    }


    @Override
    public IDuplex<ChannelData> duplex() {
        return duplex;
    }


    /**
     * read data from source successfully
     *
     * @param channelData data
     */
    private boolean onData(ChannelData channelData) {
        ChannelDataType channelDataType = ChannelDataType.values()[channelData.getType()];
        String channelId = channelData.getChannelId();
        String resourceId = channelData.getResourceId();

        switch (channelDataType) {
            // init channel event
            case ChannelInit:
                IChannel channel = new DefaultChannel(false, channelId, resourceId, this);
                remoteChannelMap.put(channelId, channel);
                onAccept.accept(channel);
                break;
            // close channel event
            case ChannelClose:
                if( channelData.isLocalChannel() ) {
                    remoteChannelMap.getOrDefault(channelId, IChannel.EmptyChannel).duplex().close();
                }else{
                    localChannelMap.getOrDefault(channelId, IChannel.EmptyChannel).duplex().close();
                }
                break;
            // data from remote
            case NormalData:
                if (channelData.isLocalChannel()) {     // 数据来自对端的本地channel
                    remoteChannelMap.get(channelId).duplex().push(channelData.getPayload());
                } else {                                  // 数据来自对端的远程channel的镜像
                    localChannelMap.get(channelId).duplex().push(channelData.getPayload());
                }
                break;
            default:
                // unreachable, ignore
        }
        return false;
    }

    /**
     * When the multiplex is closed, it will invoke this functional method.
     *
     * @param callback callback
     */
    @Override
    public IMultiplex onClosed(Consumer<Throwable> callback) {
        this.onClosed = callback;
        return this;
    }

    /**
     * when the underlying duplex closed
     */
    private void onDuplexClosed(Throwable throwable) {
        destroy(throwable);
        if (onClosed != null)
            onClosed.accept(null);
    }

}
