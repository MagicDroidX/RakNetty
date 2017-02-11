package com.magicdroidx.raknetty.protocol.raknet.session;

import com.magicdroidx.raknetty.RakNetty;
import io.netty.buffer.ByteBuf;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public final class ConnectionRequestPacket1 extends SessionPacket implements FramelessPacket{
    public static final int ID = 0x05;

    @SuppressWarnings({"PointlessArithmeticExpression", "WeakerAccess"})
    public static final int MTU_PADDING = 0
            + 1    // Packet Id
            + 16   // Offline Message Data Id
            + 1;   // Protocol Version


    public int protocolVersion;
    public int MTU;

    public ConnectionRequestPacket1() {
        super(ConnectionRequestPacket1.ID);
    }

    public ConnectionRequestPacket1(ByteBuf buf) {
        super(buf);
    }

    @Override
    public void decode() {
        super.decode();
        skipBytes(RakNetty.OFFLINE_MESSAGE_DATA_ID.length);
        protocolVersion = readUnsignedByte();
        MTU = readableBytes() + MTU_PADDING;
    }

    @Override
    public void encode() {
        super.encode();
        writeBytes(RakNetty.OFFLINE_MESSAGE_DATA_ID);
        writeByte(protocolVersion);
        writeZero(MTU - MTU_PADDING);
    }
}
