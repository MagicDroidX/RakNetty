package com.magicdroidx.raknetty;

import com.magicdroidx.raknetty.handler.UnconnectedPingHandler;
import com.magicdroidx.raknetty.handler.codec.RakNetPacketDecoder;
import com.magicdroidx.raknetty.handler.codec.RakNetPacketEncoder;
import com.magicdroidx.raknetty.handler.session.SessionManager;
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
    int port = 19132;
    private int mtu;

    private InetSocketAddress[] systemAddresses;

    private ServerListener listener;

    public RakNetServer() throws IOException {
        //Get mtu
        mtu = NetworkInterfaceUtil.getMTU();

        //Get System Addresses
        systemAddresses = NetworkInterfaceUtil.getSystemAddresses(port);
    }

    public void run() throws InterruptedException {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(boss)
                    .channel(NioDatagramChannel.class)
                    .handler(new ChannelInitializer<DatagramChannel>() {
                        protected void initChannel(DatagramChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("RakNetDecoder", new RakNetPacketDecoder());
                            pipeline.addLast("RakNetEncoder", new RakNetPacketEncoder());
                            pipeline.addLast("UnconnectedPingHandler", new UnconnectedPingHandler(RakNetServer.this));
                            pipeline.addLast(worker, "SessionHandler", new SessionManager(RakNetServer.this));
                            pipeline.addLast("Unhandled", new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    AddressedRakNetPacket packet = (AddressedRakNetPacket) msg;
                                    RakNetPacket buf = (RakNetPacket) packet.content();
                                    System.out.println("Unhandled: " + buf);
                                    /*byte[] bytes = new byte[buf.writerIndex()];
                                    buf.getBytes(0, bytes);
                                    System.out.println("Unhandled: " + BaseEncoding.base16().withSeparator(" ", 2).encode(bytes));*/
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
            future.channel().closeFuture().await();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public void setListener(ServerListener listener) {
        this.listener = listener;
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
}
