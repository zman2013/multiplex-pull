package com.zman.stream.multiplex.pull;

import com.zman.pull.stream.IDuplex;
import com.zman.pull.stream.impl.DefaultDuplex;

/**
 * IChannel is an alias for {@link IDuplex}.
 */
public interface IChannel {

    /**
     * @return  unique id for this channel
     */
    String id();

    /**
     *
     * Which resource this channel supports, for example: signalModel、bleModel、AccountModel, etc.
     *
     * There is only two channels(local & remote) with the unique resourceId in one multiplex.
     *
     * @return the unique resource id in all systems
     */
    String resourceId();

    /**
     * close duplex, and send close event to multiplex
     */
    void close();

    IDuplex<byte[]> duplex();


    IChannel EmptyChannel = new IChannel() {
        @Override
        public String id() {
            return null;
        }

        @Override
        public String resourceId() {
            return null;
        }

        @Override
        public void close() {

        }

        @Override
        public IDuplex<byte[]> duplex() {
            return new DefaultDuplex<>();
        }
    };
}
