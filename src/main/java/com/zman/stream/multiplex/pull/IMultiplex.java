package com.zman.stream.multiplex.pull;

import com.zman.pull.stream.IDuplex;
import com.zman.stream.multiplex.pull.domain.ChannelData;

import java.util.function.Consumer;

/**
 * The multiplex is paired between two nodes, one called local multiplex, the other called remote multiplex.
 *
 * You can create a local channel from one multiplex, the paired remote multiplex will create a remote channel automatically.
 *
 * The channel pair will be linked automatically.
 *
 * The multiplex will maintain a local channel list and a remote channel list.
 * The local channel is created by local multiplex, the remote channel is created by remote multiplex.
 * One local channel of local multiplex is paired with one remote channel of the remote multiplex.
 *
 */
public interface IMultiplex {


    /**
     * create a local channel and trigger remote multiplex creating a remote channel.
     *
     * @param resourceId source unique id in the whole system
     * @return the local channel which is linked with the remote channel
     */
    IChannel createChannel(String resourceId);

    /**
     * when the multiplex received a remote channel, will invoke this callback
     * @param callback functional
     * @return self
     */
    IMultiplex onAccept(Consumer<IChannel> callback);


    /**
     * The channel will invoke {@link #pushSource(IChannel, int, byte[])} to push data into the multiplex's source buffer.
     *
     * If the multiplex's source buffer is not full, this function will return true, or else it return false.
     *
     * When the multiplex's source buffer is full, the multiplex should store the channel reference.
     * And when it has more space to accept data, it should notify the failed channel to pushSource again.
     *
     * @param channel channel info
     * @param channelDataType    channel data type
     * @param data    data
     *
     * @return true for success, false for failure
     */
    boolean pushSource(IChannel channel, int channelDataType, byte[] data);


    /**
     * destroy the multiplex.
     *
     * The function will trigger the multiplex to `send` close signal to all local and remote channels.
     *
     * The `send` means read from source with parameter end=true.
     *
     */
    void destroy();

    /**
     * @return the underlying duplex
     */
    IDuplex<ChannelData> duplex();
}
