package com.magicdroidx.raknetty.handler.session.future;

import com.magicdroidx.raknetty.buffer.RakNetByteBuf;
import com.magicdroidx.raknetty.protocol.raknet.session.FrameSetPacket;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class FrameSetPacketFuture extends PacketFuture<FrameSetPacket> {

    RakNetByteBuf buf;

    public FrameSetPacketFuture(FrameSetPacket packet, long sendTime) {
        super(packet, sendTime);
    }

    public DatagramPacket envelop(InetSocketAddress recipient) {
        if (buf == null) {
            buf = RakNetByteBuf.buffer();
            packet().write(buf);
        }

        return new DatagramPacket(buf, recipient);
    }

    @Override
    public FrameSetPacket packet() {
        return super.packet();
    }

    public int frameSetIndex() {
        return packet().index;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FrameSetPacketFuture) {
            return ((FrameSetPacketFuture) obj).frameSetIndex() == frameSetIndex();
        }

        return false;
    }
}
