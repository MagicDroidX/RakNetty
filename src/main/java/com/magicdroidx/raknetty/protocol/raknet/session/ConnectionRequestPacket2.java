package com.magicdroidx.raknetty.protocol.raknet.session;

import com.magicdroidx.raknetty.RakNetty;
import io.netty.buffer.ByteBuf;

import java.net.InetSocketAddress;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public final class ConnectionRequestPacket2 extends SessionPacket implements FramelessPacket{
    public static final int ID = 0x07;

    public InetSocketAddress serverAddress;
    public int MTU;
    public long clientGUID;

    public ConnectionRequestPacket2() {
        super(ConnectionRequestPacket2.ID);
    }

    public ConnectionRequestPacket2(ByteBuf buf) {
        super(buf);
    }

    @Override
    public void decode() {
        super.decode();
        skipBytes(RakNetty.OFFLINE_MESSAGE_DATA_ID.length);
        serverAddress = readAddress();
        MTU = readUnsignedShort();
        clientGUID = readLong();
    }

    @Override
    public void encode() {
        super.encode();
        writeBytes(RakNetty.OFFLINE_MESSAGE_DATA_ID);
        writeAddress(serverAddress);
        writeByte(MTU);
        writeLong(clientGUID);
    }
}
