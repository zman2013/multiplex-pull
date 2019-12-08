package com.zman.stream.multiplex.pull.codec;

import com.zman.pull.stream.ISink;
import com.zman.pull.stream.ISource;
import com.zman.pull.stream.bean.ReadResult;
import com.zman.pull.stream.bean.ReadResultEnum;
import com.zman.pull.stream.impl.DefaultThrough;
import com.zman.stream.multiplex.pull.domain.ChannelData;
import com.zman.stream.multiplex.pull.enums.ChannelDataType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import static com.zman.stream.multiplex.pull.codec.MultiplexEncoder.convertToBytes;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MultiplexDecoderTest {

    @Mock
    private ISource<byte[]> source;
    @Mock
    private ISink<byte[]> sink;

    @Test
    public void encodeNormalData() throws NoSuchFieldException, IllegalAccessException {
        MultiplexDecoder decoder = new MultiplexDecoder();
        Field sourceField = DefaultThrough.class.getDeclaredField("source");
        sourceField.setAccessible(true);
        sourceField.set(decoder, source);

        String channelId = "0123456789";
        String resourceId = "9876543210";
        int type = ChannelDataType.NormalData.ordinal();
        ChannelData channelData = new ChannelData(true,channelId, resourceId, type, new byte[1]);
        byte[] bytes = convertToBytes(channelData, ByteBuffer.allocate(64*1024));
        ReadResult<byte[]> readResult = new ReadResult<>(ReadResultEnum.Available, bytes);

        when(source.get(false, null, sink)).thenReturn(readResult);

        ReadResult<ChannelData> result = decoder.get(false, sink);

        // verify
        ChannelData resultData = result.data;
        Assert.assertEquals(channelId, resultData.getChannelId());
        Assert.assertEquals(resourceId, resultData.getResourceId());
        Assert.assertEquals(type, resultData.getType());
        Assert.assertArrayEquals(new byte[1], resultData.getPayload());
    }

    @Test
    public void encodeChannelInit() throws NoSuchFieldException, IllegalAccessException {
        MultiplexDecoder decoder = new MultiplexDecoder();
        Field sourceField = DefaultThrough.class.getDeclaredField("source");
        sourceField.setAccessible(true);
        sourceField.set(decoder, source);

        String channelId = "0123456789";
        String resourceId = "9876543210";
        int type = ChannelDataType.ChannelInit.ordinal();
        ChannelData channelData = new ChannelData(true, channelId, resourceId, type, new byte[0]);
        byte[] bytes = convertToBytes(channelData, ByteBuffer.allocate(64*1024));
        ReadResult<byte[]> readResult = new ReadResult<>(ReadResultEnum.Available, bytes);

        when(source.get(false, null, sink)).thenReturn(readResult);

        ReadResult<ChannelData> result = decoder.get(false, sink);

        // verify
        ChannelData resultData = result.data;
        Assert.assertEquals(channelId, resultData.getChannelId());
        Assert.assertEquals(resourceId, resultData.getResourceId());
        Assert.assertEquals(type, resultData.getType());
        Assert.assertArrayEquals(new byte[0], resultData.getPayload());
    }

    @Test
    public void encodeChannelClose() throws NoSuchFieldException, IllegalAccessException {
        MultiplexDecoder decoder = new MultiplexDecoder();
        Field sourceField = DefaultThrough.class.getDeclaredField("source");
        sourceField.setAccessible(true);
        sourceField.set(decoder, source);

        String channelId = "0123456789";
        String resourceId = "9876543210";
        int type = ChannelDataType.ChannelClose.ordinal();
        ChannelData channelData = new ChannelData(true, channelId, resourceId, type, new byte[0]);
        byte[] bytes = convertToBytes(channelData, ByteBuffer.allocate(64*1024));
        ReadResult<byte[]> readResult = new ReadResult<>(ReadResultEnum.Available, bytes);

        when(source.get(false, null, sink)).thenReturn(readResult);

        ReadResult<ChannelData> result = decoder.get(false, sink);

        // verify
        ChannelData resultData = result.data;
        Assert.assertEquals(channelId, resultData.getChannelId());
        Assert.assertEquals(resourceId, resultData.getResourceId());
        Assert.assertEquals(type, resultData.getType());
        Assert.assertArrayEquals(new byte[0], resultData.getPayload());
    }

}
