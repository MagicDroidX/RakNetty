package com.magicdroidx.raknetty.handler.session.future;

import com.magicdroidx.raknetty.protocol.raknet.Reliability;
import com.magicdroidx.raknetty.protocol.raknet.session.FramePacket;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class FramePacketFuture extends PacketFuture<FramePacket> {

    public FramePacketFuture(FramePacket packet, Reliability reliability, long sendTime) {
        super(packet, reliability, sendTime);
    }

    @Override
    public FramePacket packet() {
        return super.packet();
    }
}
