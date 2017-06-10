package com.magicdroidx.raknetty;

import com.google.common.io.BaseEncoding;
import com.magicdroidx.raknetty.handler.UnconnectedPingHandler;
import com.magicdroidx.raknetty.handler.codec.RakNetPacketDecoder;
import com.magicdroidx.raknetty.handler.codec.RakNetPacketEncoder;
import com.magicdroidx.raknetty.handler.session.SessionManager;
import com.magicdroidx.raknetty.protocol.raknet.AddressedRakNetPacket;
import com.magicdroidx.raknetty.protocol.raknet.RakNetPacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.UUID;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public class RakNetServer {

    private final UUID uuid = UUID.randomUUID();

    private int MTU;

    public RakNetServer() throws IOException {
        MTU = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getMTU();
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
                            //用户自定义Session处理器 onSessionEstablished() 完成握手建立连接事件
                            //                        onPacketReceived(GamePacket packet) 获得来自客户端的包
                            //                        sendPacket(GamePacket packet) 回复包
                            //                        onSessionClosed() 关闭连接事件
                            pipeline.addLast("Unhandled", new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    AddressedRakNetPacket packet = (AddressedRakNetPacket) msg;
                                    RakNetPacket buf = (RakNetPacket) packet.content();
                                    byte[] bytes = new byte[buf.writerIndex()];
                                    buf.getBytes(0, bytes);
                                    System.out.println("Unhandled: " + BaseEncoding.base16().withSeparator(" ", 2).encode(bytes));
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
                    .bind(19132)
                    .sync();
            future.channel().closeFuture().await();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public UUID uuid() {
        return uuid;
    }

    public int getMTU() {
        return MTU;
    }

}
