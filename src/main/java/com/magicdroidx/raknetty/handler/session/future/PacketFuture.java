package com.magicdroidx.raknetty.handler.session.future;

import com.magicdroidx.raknetty.protocol.Packet;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class PacketFuture<I extends Packet> implements Comparable<PacketFuture> {

    private I packet;
    private long sendTime;

    PacketFuture(I packet, long sendTime) {
        this.packet = packet;
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

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    @Override
    public String toString() {
        return "PacketFuture{" +
                "packet=" + packet +
                ", sendTime=" + sendTime +
                '}';
    }
}
