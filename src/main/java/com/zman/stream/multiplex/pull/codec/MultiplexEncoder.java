package com.zman.stream.multiplex.pull.codec;

import com.zman.pull.stream.ISink;
import com.zman.pull.stream.bean.ReadResult;
import com.zman.pull.stream.bean.ReadResultEnum;
import com.zman.pull.stream.impl.DefaultThrough;
import com.zman.stream.multiplex.pull.domain.ChannelData;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class MultiplexEncoder extends DefaultThrough<ChannelData, byte[]> {

    private ByteBuffer byteBuffer = ByteBuffer.allocate(64*1024);

    @Override
    public ReadResult get(boolean end, Throwable throwable, ISink sink) {
        ReadResult readResult = super.get(end, throwable, sink);

        if(ReadResultEnum.Available.equals(readResult.status)){
            ChannelData channelData = (ChannelData) readResult.data;

            readResult.data = convertToBytes(channelData, byteBuffer);
        }

        return readResult;
    }


    public static byte[] convertToBytes(ChannelData channelData, ByteBuffer byteBuffer){
        byteBuffer.clear();

        byteBuffer.put(channelData.getLocalChannel())
                .put(channelData.getChannelId().getBytes(StandardCharsets.UTF_8))
                .put(channelData.getResourceId().getBytes(StandardCharsets.UTF_8))
                .putInt(channelData.getType())
                .put(channelData.getPayload())
                .flip();

        byte[] buf = new byte[byteBuffer.remaining()];
        byteBuffer.get(buf);

        return buf;
    }
}
