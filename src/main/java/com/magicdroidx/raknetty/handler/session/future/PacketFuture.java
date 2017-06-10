package com.magicdroidx.raknetty.handler.session.future;

import com.magicdroidx.raknetty.protocol.Packet;
import io.netty.util.ReferenceCounted;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class PacketFuture<I extends Packet> implements Comparable<PacketFuture>, ReferenceCounted {

    private I packet;
    private long sendTime;

    PacketFuture(I packet, long sendTime) {
        this.packet = packet;
        this.packet.retain();
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

    /**
     * {@inheritDoc}
     */
    @Override
    public int refCnt() {
        return this.packet.refCnt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceCounted retain() {
        return this.packet.retain();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceCounted retain(int increment) {
        return this.packet.retain(increment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceCounted touch() {
        return this.packet.touch();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceCounted touch(Object hint) {
        return this.packet.touch(hint);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean release() {
        return this.packet.release();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean release(int decrement) {
        return this.packet.release(decrement);
    }
}
