package com.zman.stream.multiplex.pull.codec;

import com.zman.pull.stream.ISink;
import com.zman.pull.stream.bean.ReadResult;
import com.zman.pull.stream.bean.ReadResultEnum;
import com.zman.pull.stream.impl.DefaultThrough;
import com.zman.stream.multiplex.pull.domain.ChannelData;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MultiplexDecoder extends DefaultThrough<byte[], ChannelData> {


    @Override
    public ReadResult get(boolean end, ISink sink) {
        ReadResult readResult = super.get(end, sink);

        if(ReadResultEnum.Available.equals(readResult.status)){
            byte[] buffer = (byte[]) readResult.data;

            readResult.data = convertToBytes(buffer);
        }

        return readResult;
    }


    private ChannelData convertToBytes(byte[] bytes){

        byte localChannel = bytes[0];
        String channelId = new String(bytes, 1, 10, StandardCharsets.UTF_8);
        String resourceId = new String(bytes, 11, 10, StandardCharsets.UTF_8);
        int channelDataType = convertToInt(bytes, 21);
        bytes = Arrays.copyOfRange(bytes, 25, bytes.length);

        return new ChannelData(localChannel==1, channelId, resourceId, channelDataType, bytes);
    }


    private ByteBuffer intConverter = ByteBuffer.allocate(4);
    private int convertToInt(byte[] bytes, int from){
        intConverter.clear();
        intConverter.put(bytes, from, 4).flip();
        return intConverter.getInt();
    }


}
