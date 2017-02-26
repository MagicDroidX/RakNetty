package com.magicdroidx.raknetty.protocol.raknet.session;

import io.netty.buffer.ByteBuf;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public final class ConnectedPingPacket extends SessionPacket {
    public static final int ID = 0x00;

    public long timestamp;

    public ConnectedPingPacket() {
        super(ID);
    }

    public ConnectedPingPacket(ByteBuf buf) {
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
