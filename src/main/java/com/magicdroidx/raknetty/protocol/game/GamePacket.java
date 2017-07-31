package com.magicdroidx.raknetty.protocol.game;

import com.magicdroidx.raknetty.buffer.RakNetByteBuf;
import com.magicdroidx.raknetty.protocol.Packet;
import io.netty.buffer.ByteBuf;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class GamePacket implements Packet {

    private int id;

    public GamePacket(int id) {
        this.id = id;
    }

    public static GamePacket from(ByteBuf buf) {
        RakNetByteBuf in = RakNetByteBuf.wrappedBuffer(buf);

        int id = in.readUnsignedByte();

        GamePacket packet;
        switch (id) {
            case LoginPacket.ID:
                packet = new LoginPacket();
                break;
            default:
                return new GamePacket(id);
        }

        packet.read(in);
        return packet;
    }

    public int id() {
        return id;
    }

    @Override
    public void read(RakNetByteBuf in) {

    }

    @Override
    public void write(RakNetByteBuf out) {
        out.writeByte(id());
    }
}
