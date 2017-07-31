package com.magicdroidx.raknetty.protocol.game;

import com.google.common.base.Charsets;
import com.magicdroidx.raknetty.buffer.RakNetByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

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

    @Override
    public void read(RakNetByteBuf in) {
        super.read(in);
        protocolVersion = in.readInt();
        edition = in.readUnsignedByte();

        ByteBuf buf = in.readBytes(
                in.readUnsignedVarInt()
        );

        chainData = buf.readCharSequence(buf.readIntLE(), Charsets.UTF_8);
        skinData = buf.readCharSequence(buf.readIntLE(), Charsets.UTF_8);
        buf.release();
    }

    @Override
    public void write(RakNetByteBuf out) {
        super.write(out);
        out.writeInt(protocolVersion);
        out.writeByte(edition);

        ByteBuf buf = Unpooled.buffer();
        buf.writeIntLE(chainData.length());
        buf.writeCharSequence(chainData, Charsets.UTF_8);
        buf.writeIntLE(skinData.length());
        buf.writeCharSequence(skinData, Charsets.UTF_8);

        out.writeUnsignedVarInt(buf.writerIndex());
        out.writeBytes(buf);
        buf.release();
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
