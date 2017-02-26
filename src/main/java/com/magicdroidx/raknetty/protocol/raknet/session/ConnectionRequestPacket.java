package com.magicdroidx.raknetty.protocol.raknet.session;

import io.netty.buffer.ByteBuf;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public final class ConnectionRequestPacket extends SessionPacket {
    public static final int ID = 0x09;

    public long clientGUID;
    public long timestamp;
    public boolean hasSecurity;

    public ConnectionRequestPacket() {
        super(ConnectionRequestPacket.ID);
    }

    public ConnectionRequestPacket(ByteBuf buf) {
        super(buf);
    }

    @Override
    public void decode() {
        super.decode();
        clientGUID = readLong();
        timestamp = readLong();
        hasSecurity = readBoolean();
    }

    @Override
    public void encode() {
        super.encode();
        writeLong(clientGUID);
        writeLong(timestamp);
        writeBoolean(hasSecurity);
    }
}
