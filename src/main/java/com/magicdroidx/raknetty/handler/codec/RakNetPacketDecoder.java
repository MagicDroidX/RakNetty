package com.magicdroidx.raknetty.handler.codec;

import com.magicdroidx.raknetty.protocol.raknet.AddressedRakNetPacket;
import com.magicdroidx.raknetty.protocol.raknet.RakNetPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public class RakNetPacketDecoder extends MessageToMessageDecoder<DatagramPacket> {

    @Override
    protected final void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        ByteBuf buf = msg.content().retain();

        //System.out.println("IN:\n" + ByteBufUtil.prettyHexDump(buf));

        RakNetPacket packet = RakNetPacket.from(buf);
        buf.release();
        out.add(new AddressedRakNetPacket<>(packet, msg.recipient(), msg.sender()));
    }
}
