package com.magicdroidx.raknetty.protocol.raknet.session;

import com.magicdroidx.raknetty.RakNetty;
import com.magicdroidx.raknetty.buffer.RakNetByteBuf;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public final class OpenConnectionRequestPacket1 extends SessionPacket implements FramelessPacket {
    public static final int ID = 0x05;

    @SuppressWarnings({"PointlessArithmeticExpression", "WeakerAccess"})
    public static final int MTU_PADDING = 0
            + 1    // Packet Id
            + 16   // Offline Message Data Id
            + 1;   // Protocol Version

    public int protocolVersion;
    public int MTU;

    public OpenConnectionRequestPacket1() {
        super(OpenConnectionRequestPacket1.ID);
    }

    @Override
    public void read(RakNetByteBuf in) {
        super.read(in);
        in.skipBytes(RakNetty.OFFLINE_MESSAGE_ID.length);
        protocolVersion = in.readUnsignedByte();
        MTU = in.readableBytes() + MTU_PADDING;
    }

    @Override
    public void write(RakNetByteBuf out) {
        super.write(out);
        out.writeBytes(RakNetty.OFFLINE_MESSAGE_ID);
        out.writeByte(protocolVersion);
        out.writeZero(MTU - MTU_PADDING);
    }

    @Override
    public String toString() {
        return "OpenConnectionRequestPacket1{" +
                "protocolVersion=" + protocolVersion +
                ", MTU=" + MTU +
                '}';
    }
}
