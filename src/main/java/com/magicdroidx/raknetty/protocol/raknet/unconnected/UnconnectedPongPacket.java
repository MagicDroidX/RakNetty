package com.magicdroidx.raknetty.protocol.raknet.unconnected;

import com.magicdroidx.raknetty.RakNetty;
import com.magicdroidx.raknetty.protocol.raknet.RakNetPacket;
import io.netty.buffer.ByteBuf;

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

    public UnconnectedPongPacket(ByteBuf buf) {
        super(buf);
    }

    @Override
    public void decode() {
        super.decode();
        pingId = readLong();
        serverGUID = readLong();
        skipBytes(RakNetty.OFFLINE_MESSAGE_ID.length);
        serverName = readCharSequence();
    }

    @Override
    public void encode() {
        super.encode();
        writeLong(pingId);
        writeLong(serverGUID);
        writeBytes(RakNetty.OFFLINE_MESSAGE_ID);
        writeCharSequence(serverName);
    }
}
