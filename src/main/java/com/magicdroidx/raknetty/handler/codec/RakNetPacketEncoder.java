package com.magicdroidx.raknetty.handler.codec;

import com.magicdroidx.raknetty.buffer.RakNetByteBuf;
import com.magicdroidx.raknetty.protocol.raknet.RakNetPacket;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public class RakNetPacketEncoder extends MessageToMessageEncoder<AddressedEnvelope<RakNetPacket, InetSocketAddress>> {

    @Override
    protected final void encode(ChannelHandlerContext ctx, AddressedEnvelope<RakNetPacket, InetSocketAddress> msg, List<Object> out) throws Exception {
        assert out.isEmpty();
        RakNetPacket packet = msg.content();
        RakNetByteBuf data = RakNetByteBuf.buffer();
        packet.write(data);
        out.add(new DatagramPacket(data, msg.recipient(), msg.sender()));
    }

}
