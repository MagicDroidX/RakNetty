package com.magicdroidx.raknetty.handler.session.future;

import com.magicdroidx.raknetty.protocol.raknet.RakNetPacket;
import com.magicdroidx.raknetty.protocol.raknet.Reliability;
import com.magicdroidx.raknetty.protocol.raknet.session.FrameSetPacket;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class FrameSetPacketFuture extends PacketFuture<FrameSetPacket> {

    public FrameSetPacketFuture(FrameSetPacket packet, long sendTime) {
        super(packet, sendTime);
    }

    @Override
    public FrameSetPacket packet() {
        return super.packet();
    }
}
