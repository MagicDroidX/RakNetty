package com.magicdroidx.raknetty.protocol.raknet.session;

import com.magicdroidx.raknetty.RakNetty;
import io.netty.buffer.ByteBuf;

import java.net.InetSocketAddress;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public final class ConnectionResponsePacket2 extends SessionPacket implements FramelessPacket{
    public static final int ID = 0x08;

    public long serverGUID;
    public InetSocketAddress clientAddress;
    public int MTU;

    public ConnectionResponsePacket2() {
        super(ConnectionResponsePacket2.ID);
    }

    public ConnectionResponsePacket2(ByteBuf buf) {
        super(buf);
    }

    @Override
    public void decode() {
        super.decode();
        skipBytes(RakNetty.OFFLINE_MESSAGE_DATA_ID.length);
        serverGUID = readLong();
        clientAddress = readAddress();
        MTU = readUnsignedShort();
    }

    @Override
    public void encode() {
        super.encode();
        writeBytes(RakNetty.OFFLINE_MESSAGE_DATA_ID);
        writeLong(serverGUID);
        writeAddress(clientAddress);
        writeShort(MTU);
        writeBoolean(false); //Encryption
    }
}
