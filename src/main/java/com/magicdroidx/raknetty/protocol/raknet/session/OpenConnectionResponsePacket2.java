package com.magicdroidx.raknetty.protocol.raknet.session;

import com.magicdroidx.raknetty.RakNetty;
import com.magicdroidx.raknetty.buffer.RakNetByteBuf;

import java.net.InetSocketAddress;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public final class OpenConnectionResponsePacket2 extends SessionPacket implements FramelessPacket {
    public static final int ID = 0x08;

    public long serverGUID;
    public InetSocketAddress clientAddress;
    public int MTU;

    public OpenConnectionResponsePacket2() {
        super(OpenConnectionResponsePacket2.ID);
    }

    @Override
    public void read(RakNetByteBuf in) {
        super.read(in);
        in.skipBytes(RakNetty.OFFLINE_MESSAGE_ID.length);
        serverGUID = in.readLong();
        clientAddress = in.readAddress();
        MTU = in.readUnsignedShort();
    }

    @Override
    public void write(RakNetByteBuf out) {
        super.write(out);
        out.writeBytes(RakNetty.OFFLINE_MESSAGE_ID);
        out.writeLong(serverGUID);
        out.writeAddress(clientAddress);
        out.writeShort(MTU);
        out.writeBoolean(false); //Encryption
    }

    @Override
    public String toString() {
        return "OpenConnectionResponsePacket2{" +
                "serverGUID=" + serverGUID +
                ", clientAddress=" + clientAddress +
                ", MTU=" + MTU +
                '}';
    }
}
