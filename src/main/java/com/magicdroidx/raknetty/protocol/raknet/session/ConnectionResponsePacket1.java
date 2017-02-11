package com.magicdroidx.raknetty.protocol.raknet.session;

import com.magicdroidx.raknetty.RakNetty;
import io.netty.buffer.ByteBuf;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public final class ConnectionResponsePacket1 extends SessionPacket implements FramelessPacket{
    public static final int ID = 0x06;

    public long serverGUID;
    public int MTU;

    public ConnectionResponsePacket1() {
        super(ConnectionResponsePacket1.ID);
    }

    public ConnectionResponsePacket1(ByteBuf buf) {
        super(buf);
    }

    @Override
    public void decode() {
        super.decode();
        skipBytes(RakNetty.OFFLINE_MESSAGE_DATA_ID.length);
        serverGUID = readLong();
        skipBytes(1); //Security
        MTU = readUnsignedShort();
    }

    @Override
    public void encode() {
        super.encode();
        writeBytes(RakNetty.OFFLINE_MESSAGE_DATA_ID);
        writeLong(serverGUID);
        writeBoolean(false);
        writeShort(MTU);
    }
}
