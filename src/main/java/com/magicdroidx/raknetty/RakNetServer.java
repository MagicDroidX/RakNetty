package com.magicdroidx.raknetty;

import com.magicdroidx.raknetty.bootstrap.RakNetServerBootstrap;
import com.magicdroidx.raknetty.handler.UnconnectedPingHandler;
import com.magicdroidx.raknetty.handler.codec.RakNetPacketDecoder;
import com.magicdroidx.raknetty.handler.codec.RakNetPacketEncoder;
import com.magicdroidx.raknetty.handler.session.ServerSessionHandler;
import com.magicdroidx.raknetty.listener.ServerListener;
import com.magicdroidx.raknetty.protocol.raknet.AddressedRakNetPacket;
import com.magicdroidx.raknetty.protocol.raknet.RakNetPacket;
import com.magicdroidx.raknetty.util.NetworkInterfaceUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public class RakNetServer {

    private final UUID uuid = UUID.randomUUID();

    ServerListener listener;

    int port = 19132;

    private int mtu;

    private InetSocketAddress[] systemAddresses;

    private Channel channel;

    private ServerSessionHandler sessionHandler;

    private NioEventLoopGroup bossGroup;

    private NioEventLoopGroup workerGroup;

    private RakNetServer() throws IOException {
        //Get mtu
        mtu = NetworkInterfaceUtil.getMTU();

        //Get System Addresses
        systemAddresses = NetworkInterfaceUtil.getSystemAddresses(port);

        sessionHandler = new ServerSessionHandler(RakNetServer.this);
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
    }

    public static ServerBootstrap bootstrap() {
        return new ServerBootstrap();
    }

    void start() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(bossGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(new ChannelInitializer<DatagramChannel>() {
                    protected void initChannel(DatagramChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("RakNetDecoder", new RakNetPacketDecoder());
                        pipeline.addLast("RakNetEncoder", new RakNetPacketEncoder());
                        pipeline.addLast(workerGroup, "UnconnectedPingHandler", new UnconnectedPingHandler(RakNetServer.this));
                        pipeline.addLast(workerGroup, "ServerSessionHandler", sessionHandler);
                        pipeline.addLast("Unhandled", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                AddressedRakNetPacket packet = (AddressedRakNetPacket) msg;
                                RakNetPacket buf = (RakNetPacket) packet.content();
                                System.out.println("Unhandled Inbound Packet: " + buf);
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                super.exceptionCaught(ctx, cause);
                                //Block
                            }
                        });
                    }
                });

        ChannelFuture future = bootstrap
                .bind(port)
                .sync();
        channel = future.channel();
    }

    public void stop() {
        sessionHandler.closeAll();
        channel.close().awaitUninterruptibly();
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    public ServerListener listener() {
        return listener;
    }

    public UUID uuid() {
        return uuid;
    }

    public int mtu() {
        return mtu;
    }

    public InetSocketAddress[] systemAddresses() {
        return systemAddresses;
    }

    public static class ServerBootstrap implements RakNetServerBootstrap {

        private int port = 19132;
        private ServerListener listener;

        private ServerBootstrap() {
        }

        @Override
        public ServerBootstrap withPort(int port) {
            this.port = port;
            return this;
        }

        @Override
        public ServerBootstrap withListener(ServerListener listener) {
            this.listener = listener;
            return this;
        }

        private RakNetServer build() throws IOException {
            RakNetServer server = new RakNetServer();
            server.port = port;
            server.listener = listener;
            return server;
        }

        public RakNetServer start() throws IOException, InterruptedException {
            RakNetServer server = build();
            server.start();
            return server;
        }
    }
}
