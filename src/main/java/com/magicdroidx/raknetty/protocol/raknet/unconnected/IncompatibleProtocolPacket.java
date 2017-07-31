package com.magicdroidx.raknetty.protocol.raknet.unconnected;

import com.magicdroidx.raknetty.RakNetty;
import com.magicdroidx.raknetty.buffer.RakNetByteBuf;
import com.magicdroidx.raknetty.protocol.raknet.RakNetPacket;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public final class IncompatibleProtocolPacket extends RakNetPacket {
    public static final int ID = 0x19;

    public int protocolVersion;
    public long serverGUID;

    public IncompatibleProtocolPacket() {
        super(IncompatibleProtocolPacket.ID);
    }

    @Override
    public void read(RakNetByteBuf in) {
        super.read(in);
        protocolVersion = in.readUnsignedByte();
        in.skipBytes(RakNetty.OFFLINE_MESSAGE_ID.length);
        serverGUID = in.readLong();
    }

    @Override
    public void write(RakNetByteBuf out) {
        super.write(out);
        out.writeByte(protocolVersion);
        out.writeBytes(RakNetty.OFFLINE_MESSAGE_ID);
        out.writeLong(serverGUID);
    }

    @Override
    public String toString() {
        return "IncompatibleProtocolPacket{" +
                "protocolVersion=" + protocolVersion +
                ", serverGUID=" + serverGUID +
                '}';
    }
}
