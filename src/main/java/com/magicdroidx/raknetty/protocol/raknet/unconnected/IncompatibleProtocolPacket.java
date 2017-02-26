package com.magicdroidx.raknetty.protocol.raknet.unconnected;

import com.magicdroidx.raknetty.RakNetty;
import com.magicdroidx.raknetty.protocol.raknet.RakNetPacket;
import io.netty.buffer.ByteBuf;

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

    public IncompatibleProtocolPacket(ByteBuf buf) {
        super(buf);
    }

    @Override
    public void decode() {
        super.decode();
        protocolVersion = readUnsignedByte();
        skipBytes(RakNetty.OFFLINE_MESSAGE_ID.length);
        serverGUID = readLong();
    }

    @Override
    public void encode() {
        super.encode();
        writeByte(protocolVersion);
        writeBytes(RakNetty.OFFLINE_MESSAGE_ID);
        writeLong(serverGUID);
    }
}
