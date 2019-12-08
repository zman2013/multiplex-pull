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
public class MultiplexEncoderTest {
    @Mock
    private ISource<ChannelData> source;
    @Mock
    private ISink<ChannelData> sink;

    @Test
    public void encodeNormalData() throws NoSuchFieldException, IllegalAccessException {
        MultiplexEncoder encoder = new MultiplexEncoder();
        Field sourceField = DefaultThrough.class.getDeclaredField("source");
        sourceField.setAccessible(true);
        sourceField.set(encoder, source);

        String channelId = "0123456789";
        String resourceId = "9876543210";
        int type = ChannelDataType.NormalData.ordinal();
        ChannelData channelData = new ChannelData(true, channelId, resourceId, type, new byte[0]);
        byte[] expectedBytes = convertToBytes(channelData, ByteBuffer.allocate(64*1024));
        ReadResult<ChannelData> readResult = new ReadResult<>(ReadResultEnum.Available, channelData);

        when(source.get(false, null, sink)).thenReturn(readResult);

        ReadResult<byte[]> result = encoder.get(false, null, sink);

        // verify
        Assert.assertArrayEquals(expectedBytes, result.data);
    }
}
