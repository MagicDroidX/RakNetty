package com.magicdroidx.raknetty.protocol.raknet.session;

import com.magicdroidx.raknetty.RakNetty;
import com.magicdroidx.raknetty.buffer.RakNetByteBuf;

import java.net.InetSocketAddress;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public final class OpenConnectionRequestPacket2 extends SessionPacket implements FramelessPacket {
    public static final int ID = 0x07;

    public InetSocketAddress serverAddress;
    public int MTU;
    public long clientGUID;

    public OpenConnectionRequestPacket2() {
        super(OpenConnectionRequestPacket2.ID);
    }

    @Override
    public void read(RakNetByteBuf in) {
        super.read(in);
        in.skipBytes(RakNetty.OFFLINE_MESSAGE_ID.length);
        serverAddress = in.readAddress();
        MTU = in.readUnsignedShort();
        clientGUID = in.readLong();
    }

    @Override
    public void write(RakNetByteBuf out) {
        super.write(out);
        out.writeBytes(RakNetty.OFFLINE_MESSAGE_ID);
        out.writeAddress(serverAddress);
        out.writeByte(MTU);
        out.writeLong(clientGUID);
    }

    @Override
    public String toString() {
        return "OpenConnectionRequestPacket2{" +
                "serverAddress=" + serverAddress +
                ", MTU=" + MTU +
                ", clientGUID=" + clientGUID +
                '}';
    }
}
