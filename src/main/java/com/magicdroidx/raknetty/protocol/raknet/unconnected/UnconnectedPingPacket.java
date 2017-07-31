package com.magicdroidx.raknetty.protocol.raknet.unconnected;

import com.magicdroidx.raknetty.RakNetty;
import com.magicdroidx.raknetty.buffer.RakNetByteBuf;
import com.magicdroidx.raknetty.protocol.raknet.RakNetPacket;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public final class UnconnectedPingPacket extends RakNetPacket {
    public static final int ID = 0x01;

    public long pingId;
    public long clientGUID;

    public UnconnectedPingPacket() {
        super(UnconnectedPingPacket.ID);
    }

    @Override
    public void read(RakNetByteBuf in) {
        super.read(in);
        pingId = in.readLong();
        in.skipBytes(RakNetty.OFFLINE_MESSAGE_ID.length);
        clientGUID = in.readLong();
    }

    @Override
    public void write(RakNetByteBuf out) {
        super.write(out);
        out.writeLong(pingId);
        out.writeBytes(RakNetty.OFFLINE_MESSAGE_ID);
        out.writeLong(clientGUID);
    }

    @Override
    public String toString() {
        return "UnconnectedPingPacket{" +
                "pingId=" + pingId +
                ", clientGUID=" + clientGUID +
                '}';
    }
}
