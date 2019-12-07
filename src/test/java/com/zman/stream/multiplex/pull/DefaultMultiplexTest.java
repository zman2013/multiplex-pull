package com.zman.stream.multiplex.pull;

import com.zman.pull.stream.IDuplex;
import com.zman.pull.stream.ISource;
import com.zman.pull.stream.impl.DefaultDuplex;
import com.zman.pull.stream.impl.DefaultSource;
import com.zman.stream.multiplex.pull.impl.DefaultMultiplex;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.xml.ws.Holder;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import static com.zman.pull.stream.util.Pull.link;
import static com.zman.pull.stream.util.Pull.pull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DefaultMultiplexTest {

    @Mock
    private Consumer<IChannel> onAccept;

    @Test
    public void createChannel(){

        IMultiplex ma = new DefaultMultiplex()
                .onAccept(onAccept);

        IMultiplex mb = new DefaultMultiplex();

        link(ma.duplex(), mb.duplex());

        mb.createChannel("food-model");

        verify(onAccept, times(1)).accept(any(IChannel.class));
    }

    @Test
    public void closeChannel(){

        Holder<Boolean> closed = new Holder<>();

        IDuplex<byte[]> duplex = new DefaultDuplex<>(
                data->{},
                ()-> closed.value = true,
                throwable -> {}
        );

        Holder<Boolean> channelLinked = new Holder<>();
        IMultiplex ma = new DefaultMultiplex()
                .onAccept(channel -> {
                    link(channel.duplex(), duplex);
                    channelLinked.value = true;
                });

        IMultiplex mb = new DefaultMultiplex();

        link(ma.duplex(), mb.duplex());

        IChannel channelB = mb.createChannel("food-model");
        channelB.close();

        // verify
        Assert.assertTrue(channelLinked.value);
        Assert.assertTrue(closed.value);

    }

    @Test
    public void normalData(){
        Holder<String> dataHolder = new Holder<>();
        Holder<Boolean> closed = new Holder<>();

        IDuplex<byte[]> duplex = new DefaultDuplex<>(
                data-> dataHolder.value = new String(data, StandardCharsets.UTF_8),
                ()-> closed.value = true,
                throwable -> {}
        );

        Holder<Boolean> channelLinked = new Holder<>();
        IMultiplex ma = new DefaultMultiplex()
                .onAccept(channel -> {
                    link(channel.duplex(), duplex);
                    channelLinked.value = true;
                });

        IMultiplex mb = new DefaultMultiplex();

        link(ma.duplex(), mb.duplex());

        IChannel channelB = mb.createChannel("food-model");

        ISource<byte[]> source = new DefaultSource<>();
        pull(source, channelB.duplex());

        source.push("hello".getBytes(StandardCharsets.UTF_8));
        channelB.duplex().close();

        // verify
        Assert.assertTrue(channelLinked.value);
        Assert.assertEquals("hello", dataHolder.value);
        Assert.assertTrue(closed.value);

    }

    @Test
    public void destroy(){
        Holder<String> dataHolder = new Holder<>();
        Holder<Boolean> closed = new Holder<>();

        IDuplex<byte[]> duplex = new DefaultDuplex<>(
                data-> dataHolder.value = new String(data, StandardCharsets.UTF_8),
                ()-> closed.value = true,
                throwable -> {}
        );

        Holder<Boolean> channelLinked = new Holder<>();
        IMultiplex ma = new DefaultMultiplex()
                .onAccept(channel -> {
                    link(channel.duplex(), duplex);
                    channelLinked.value = true;
                });

        IMultiplex mb = new DefaultMultiplex();
        link(ma.duplex(), mb.duplex());

        IChannel channelB = mb.createChannel("food-model");

        ISource<byte[]> source = new DefaultSource<>();
        pull(source, channelB.duplex());

        mb.destroy();

        // verify
        Assert.assertTrue(channelLinked.value);
        Assert.assertTrue(closed.value);

    }

}
