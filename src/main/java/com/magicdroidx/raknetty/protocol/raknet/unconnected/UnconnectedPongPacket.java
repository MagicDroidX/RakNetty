package com.magicdroidx.raknetty.protocol.raknet.unconnected;

import com.magicdroidx.raknetty.RakNetty;
import com.magicdroidx.raknetty.buffer.RakNetByteBuf;
import com.magicdroidx.raknetty.protocol.raknet.RakNetPacket;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public class UnconnectedPongPacket extends RakNetPacket {
    public static final int ID = 0x1c;

    public long pingId;
    public long serverGUID;
    public CharSequence serverName;

    public UnconnectedPongPacket() {
        super(UnconnectedPongPacket.ID);
    }

    @Override
    public void read(RakNetByteBuf in) {
        super.read(in);
        pingId = in.readLong();
        serverGUID = in.readLong();
        in.skipBytes(RakNetty.OFFLINE_MESSAGE_ID.length);
        serverName = in.readFixedString();
    }

    @Override
    public void write(RakNetByteBuf out) {
        super.write(out);
        out.writeLong(pingId);
        out.writeLong(serverGUID);
        out.writeBytes(RakNetty.OFFLINE_MESSAGE_ID);
        out.writeFixedString(serverName);
    }

    @Override
    public String toString() {
        return "UnconnectedPongPacket{" +
                "pingId=" + pingId +
                ", serverGUID=" + serverGUID +
                ", serverName=" + serverName +
                '}';
    }
}
