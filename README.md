# multiplex-pull
combine multiple pull-streams into one share underlying stream, such as one socket-pull stream can support many duplexes.

## dependency
 ```xml
 <dependency>
     <groupId>com.zmannotes</groupId>
     <artifactId>multiplex-pull</artifactId>
     <version>1.0.0</version>
</dependency>
 ```
 
## example
```java
IDuplex<byte[]> duplex = new DefaultDuplex<>();

IMultiplex ma = new DefaultMultiplex()
        .onAccept(channel -> {
            link(channel.duplex(), duplex);
        });

IMultiplex mb = new DefaultMultiplex();

link(ma.duplex(), mb.duplex());

// you can create many channels based on one multiplex
IChannel channelB = mb.createChannel("food-model");
ISource<byte[]> source = new DefaultSource<>();
pull(source, channelB.duplex());

```


