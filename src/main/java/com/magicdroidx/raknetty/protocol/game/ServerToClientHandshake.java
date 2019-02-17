package com.magicdroidx.raknetty.protocol.game;

import com.magicdroidx.raknetty.buffer.RakNetByteBuf;
import com.magicdroidx.raknetty.protocol.raknet.session.SessionPacket;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class ServerToClientHandshake extends SessionPacket {
    public static final int ID = 0x03;

    public CharSequence token;

    public ServerToClientHandshake() {
        super(ServerToClientHandshake.ID);
    }

    @Override
    public void read(RakNetByteBuf in) {
        super.read(in);
        token = in.readString();
    }

    @Override
    public void write(RakNetByteBuf out) {
        super.write(out);
        out.writeString(token);
    }

    @Override
    public String toString() {
        return "ServerToClientHandshake{" +
                "token=" + token +
                '}';
    }
}
