package com.magicdroidx.raknetty.protocol.game;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class LoginPacket extends GamePacket {
    public static final int ID = 0x01;

    public int protocolVersion;
    public int edition;
    public CharSequence chainData;
    public CharSequence skinData;


    public LoginPacket() {
        super(LoginPacket.ID);
    }

    public LoginPacket(ByteBuf buf) {
        super(buf);
    }

    @Override
    public void decode() {
        super.decode();
        System.out.println("I am daq login");
        protocolVersion = readInt();
        edition = readUnsignedByte();

        ByteBuf buf = readBytes(readUnsignedVarInt());
        chainData = buf.readCharSequence(buf.readIntLE(), Charsets.UTF_8);
        skinData = buf.readCharSequence(buf.readIntLE(), Charsets.UTF_8);

        System.out.println(toString());
    }

    @Override
    public String toString() {
        return "LoginPacket{" +
                "protocolVersion=" + protocolVersion +
                ", edition=" + edition +
                ", chainData=" + chainData +
                ", skinData=" + skinData +
                '}';
    }
}
