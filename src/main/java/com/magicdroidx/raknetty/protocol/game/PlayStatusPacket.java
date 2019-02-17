package com.magicdroidx.raknetty.protocol.game;

import com.magicdroidx.raknetty.buffer.RakNetByteBuf;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class PlayStatusPacket extends GamePacket {
    public static final int ID = 0x02;

    public static final int LOGIN_SUCCESS = 0;
    public static final int LOGIN_FAILED_CLIENT = 1;
    public static final int LOGIN_FAILED_SERVER = 2;
    public static final int PLAYER_SPAWN = 3;
    public static final int LOGIN_FAILED_INVALID_TENANT = 4;
    public static final int LOGIN_FAILED_VANILLA_EDU = 5;
    public static final int LOGIN_FAILED_EDU_VANILLA = 6;
    public static final int LOGIN_FAILED_SERVER_FULL = 7;

    public int status;

    public PlayStatusPacket() {
        super(PlayStatusPacket.ID);
    }

    @Override
    public void read(RakNetByteBuf in) {
        super.read(in);
        status = in.readInt();
    }

    @Override
    public void write(RakNetByteBuf out) {
        super.write(out);
        out.writeInt(status);
    }

    @Override
    public String toString() {
        return "PlayStatusPacket{" +
                "status=" + status +
                '}';
    }
}
