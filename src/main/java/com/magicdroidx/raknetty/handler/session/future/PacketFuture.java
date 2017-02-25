package com.magicdroidx.raknetty.handler.session.future;

import com.magicdroidx.raknetty.protocol.Packet;
import com.magicdroidx.raknetty.protocol.raknet.Reliability;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class PacketFuture<I extends Packet> implements Comparable<PacketFuture> {

    private I packet;
    private Reliability reliability;
    private long sendTime;

    PacketFuture(I packet, Reliability reliability, long sendTime) {
        this.packet = packet;
        this.reliability = reliability;
        this.sendTime = sendTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int compareTo(PacketFuture o) {
        return (int) (sendTime - o.sendTime());
    }

    public I packet() {
        return packet;
    }

    public long sendTime() {
        return sendTime;
    }

    public void sendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public Reliability reliability() {
        return reliability;
    }
}
