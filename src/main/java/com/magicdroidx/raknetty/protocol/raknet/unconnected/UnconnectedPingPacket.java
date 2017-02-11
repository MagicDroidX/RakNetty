package com.magicdroidx.raknetty.protocol.raknet.unconnected;

import com.magicdroidx.raknetty.RakNetty;
import com.magicdroidx.raknetty.protocol.raknet.RakNetPacket;
import io.netty.buffer.ByteBuf;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public final class UnconnectedPingPacket extends RakNetPacket {
    public static final int ID = 0x01;

    public long pingId;
    public long clientGUID;

    public UnconnectedPingPacket() {
        super(UnconnectedPingPacket.ID);
    }

    public UnconnectedPingPacket(ByteBuf buf) {
        super(buf);
    }

    @Override
    public void decode() {
        super.decode();
        pingId = readLong();
        skipBytes(RakNetty.OFFLINE_MESSAGE_DATA_ID.length);
        clientGUID = readLong();
    }

    @Override
    public void encode() {
        super.encode();
        writeLong(pingId);
        writeBytes(RakNetty.OFFLINE_MESSAGE_DATA_ID);
        writeLong(clientGUID);
    }
}
