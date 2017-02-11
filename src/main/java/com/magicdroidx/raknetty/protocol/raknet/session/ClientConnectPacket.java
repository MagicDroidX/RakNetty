package com.magicdroidx.raknetty.protocol.raknet.session;

import io.netty.buffer.ByteBuf;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public final class ClientConnectPacket extends SessionPacket {
    public static final int ID = 0x09;

    public long clientGUID;
    public long timestamp;
    public boolean useSecurity;

    public ClientConnectPacket() {
        super(ClientConnectPacket.ID);
    }

    public ClientConnectPacket(ByteBuf buf) {
        super(buf);
    }

    @Override
    public void decode() {
        super.decode();
        clientGUID = readLong();
        timestamp = readLong();
        useSecurity = readBoolean();
    }

    @Override
    public void encode() {
        super.encode();
        writeLong(clientGUID);
        writeLong(timestamp);
        writeBoolean(useSecurity);
    }
}
