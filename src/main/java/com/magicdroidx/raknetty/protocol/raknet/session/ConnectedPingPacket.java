package com.magicdroidx.raknetty.protocol.raknet.session;

import com.magicdroidx.raknetty.buffer.RakNetByteBuf;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public final class ConnectedPingPacket extends SessionPacket {
    public static final int ID = 0x00;

    public long timestamp;

    public ConnectedPingPacket() {
        super(ID);
    }

    @Override
    public void read(RakNetByteBuf in) {
        super.read(in);
        timestamp = in.readLong();
    }

    @Override
    public void write(RakNetByteBuf out) {
        super.write(out);
        out.writeLong(timestamp);
    }

    @Override
    public String toString() {
        return "ConnectedPingPacket{" +
                "timestamp=" + timestamp +
                '}';
    }
}
