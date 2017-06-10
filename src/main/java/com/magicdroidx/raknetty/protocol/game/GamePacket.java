package com.magicdroidx.raknetty.protocol.game;

import com.magicdroidx.raknetty.protocol.Packet;
import io.netty.buffer.ByteBuf;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class GamePacket extends Packet {

    public static GamePacket from(ByteBuf buf) {
        switch (buf.getUnsignedByte(0)) {
            case LoginPacket.ID:
                return new LoginPacket(buf);
            default:
                return new GamePacket(buf);
        }
    }

    public GamePacket(int id) {
        super(id);
    }

    public GamePacket(ByteBuf buf) {
        super(buf);
    }
}
