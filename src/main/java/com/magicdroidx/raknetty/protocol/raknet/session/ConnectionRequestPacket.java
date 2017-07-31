package com.magicdroidx.raknetty.protocol.raknet.session;

import com.magicdroidx.raknetty.buffer.RakNetByteBuf;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public final class ConnectionRequestPacket extends SessionPacket {
    public static final int ID = 0x09;

    public long clientGUID;
    public long timestamp;
    public boolean hasSecurity;

    public ConnectionRequestPacket() {
        super(ConnectionRequestPacket.ID);
    }

    @Override
    public void read(RakNetByteBuf in) {
        super.read(in);
        clientGUID = in.readLong();
        timestamp = in.readLong();
        hasSecurity = in.readBoolean();
    }

    @Override
    public void write(RakNetByteBuf out) {
        super.write(out);
        out.writeLong(clientGUID);
        out.writeLong(timestamp);
        out.writeBoolean(hasSecurity);
    }

    @Override
    public String toString() {
        return "ConnectionRequestPacket{" +
                "clientGUID=" + clientGUID +
                ", timestamp=" + timestamp +
                ", hasSecurity=" + hasSecurity +
                '}';
    }
}
