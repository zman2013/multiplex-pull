package com.zman.stream.multiplex.pull;

import com.zman.pull.stream.IDuplex;
import com.zman.pull.stream.impl.DefaultDuplex;

/**
 * IChannel is an alias for {@link IDuplex}.
 */
public interface IChannel {

    /**
     * The local channel is created by local multiplex; the remote channel is created by remote multiplex.
     * @return true: local channel, false: mirror channel of the remote channel
     */
    boolean isLocalChannel();

    /**
     * @return  unique id for this channel, 10 ascii characters
     */
    String id();

    /**
     *
     * Which resource this channel supports, for example: signalModel、bleModel、AccountModel, etc.
     *
     * There is only two channels(local and remote) with the unique resourceId in one multiplex.
     *
     * @return the unique resource id in all systems, 10 ascii characters
     */
    String resourceId();

    /**
     * close duplex, and send close event to multiplex
     */
    void close();

    /**
     * @return the underlying duplex
     */
    IDuplex<byte[]> duplex();


    IChannel EmptyChannel = new IChannel() {
        public boolean isLocalChannel() {
            return true;
        }
        @Override
        public String id() {
            return null;
        }

        @Override
        public String resourceId() {
            return null;
        }

        @Override
        public void close() {}

        @Override
        public IDuplex<byte[]> duplex() {
            return new DefaultDuplex<>();
        }
    };
}
