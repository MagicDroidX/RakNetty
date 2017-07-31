package com.magicdroidx.raknetty.protocol.raknet.session;

import com.magicdroidx.raknetty.RakNetty;
import com.magicdroidx.raknetty.buffer.RakNetByteBuf;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public final class OpenConnectionResponsePacket1 extends SessionPacket implements FramelessPacket {
    public static final int ID = 0x06;

    public long serverGUID;
    public int MTU;

    public OpenConnectionResponsePacket1() {
        super(OpenConnectionResponsePacket1.ID);
    }

    @Override
    public void read(RakNetByteBuf in) {
        super.read(in);
        in.skipBytes(RakNetty.OFFLINE_MESSAGE_ID.length);
        serverGUID = in.readLong();
        in.skipBytes(1); //Security
        MTU = in.readUnsignedShort();
    }

    @Override
    public void write(RakNetByteBuf out) {
        super.write(out);
        out.writeBytes(RakNetty.OFFLINE_MESSAGE_ID);
        out.writeLong(serverGUID);
        out.writeBoolean(false);
        out.writeShort(MTU);
    }

    @Override
    public String toString() {
        return "OpenConnectionResponsePacket1{" +
                "serverGUID=" + serverGUID +
                ", MTU=" + MTU +
                '}';
    }
}
