package com.magicdroidx.raknetty.handler;

import com.magicdroidx.raknetty.protocol.raknet.AddressedRakNetPacket;
import com.magicdroidx.raknetty.protocol.raknet.RakNetPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public abstract class RakNetInboundHandler<I extends RakNetPacket> extends SimpleChannelInboundHandler<AddressedRakNetPacket<I>> {

    private Class<I> packetClass;

    public RakNetInboundHandler(Class<I> packetClass) {
        this.packetClass = packetClass;
    }

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        //noinspection SimplifiableIfStatement
        if (super.acceptInboundMessage(msg) && msg instanceof AddressedRakNetPacket) {
            return this.packetClass.isAssignableFrom(((AddressedRakNetPacket) msg).content().getClass());
        }
        return false;
    }

    @Override
    protected final void channelRead0(ChannelHandlerContext ctx, AddressedRakNetPacket<I> packet) throws Exception {
        packetReceived(ctx, packet);
        packet.release();
    }

    protected abstract void packetReceived(ChannelHandlerContext ctx, AddressedRakNetPacket<I> p);
}
