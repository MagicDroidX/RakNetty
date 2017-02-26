package com.magicdroidx.raknetty.handler.session.future;

import com.magicdroidx.raknetty.protocol.raknet.Reliability;
import com.magicdroidx.raknetty.protocol.raknet.session.FramePacket;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class FramePacketFuture extends PacketFuture<FramePacket> {

    private Reliability reliability;

    public FramePacketFuture(FramePacket packet, long sendTime, Reliability reliability) {
        super(packet, sendTime);
        this.reliability = reliability;
    }

    @Override
    public FramePacket packet() {
        return super.packet();
    }

    public Reliability reliability() {
        return reliability;
    }
}
