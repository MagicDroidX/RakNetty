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

        int id = in.readUnsignedVarInt();

        GamePacket packet;
        switch (id) {
            case LoginPacket.ID:
                packet = new LoginPacket();
                break;
            case PlayStatusPacket.ID:
                packet = new PlayStatusPacket();
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
        out.writeUnsignedVarInt(id());
    }

    @Override
    public String toString() {
        return "GamePacket{" +
                "id=" + id +
                '}';
    }
}
