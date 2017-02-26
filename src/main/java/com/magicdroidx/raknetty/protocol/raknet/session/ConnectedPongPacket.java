package com.magicdroidx.raknetty.protocol.raknet.session;

import io.netty.buffer.ByteBuf;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public final class ConnectedPongPacket extends SessionPacket {
    public static final int ID = 0x03;

    public long timestamp;

    public ConnectedPongPacket() {
        super(ID);
    }

    public ConnectedPongPacket(ByteBuf buf) {
        super(buf);
    }

    @Override
    public void decode() {
        super.decode();
        timestamp = readLong();
    }

    @Override
    public void encode() {
        super.encode();
        writeLong(timestamp);
    }
}
