package com.magicdroidx.raknetty.protocol.raknet.session;

import com.magicdroidx.raknetty.io.VarIntInputStream;
import com.magicdroidx.raknetty.io.VarIntOutputStream;
import com.magicdroidx.raknetty.protocol.game.GamePacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class GameWrapperPacket extends SessionPacket {
    public static final int ID = 0xFE;

    public GamePacket body;

    public GameWrapperPacket() {
        super(GameWrapperPacket.ID);
    }

    public GameWrapperPacket(ByteBuf buf) {
        super(buf);
    }

    @Override
    public void decode() {
        super.decode();
        VarIntInputStream in = new VarIntInputStream(new BufferedInputStream(new InflaterInputStream(new ByteBufInputStream(this))));

        try {
            int bodySize = in.readUnsignedVarInt();
            byte[] bytes = new byte[bodySize];
            in.read(bytes);
            body = GamePacket.from(Unpooled.wrappedBuffer(bytes));
            body.decode();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void encode() {
        super.encode();
        body.encode();
        VarIntOutputStream out = new VarIntOutputStream(new BufferedOutputStream(new DeflaterOutputStream(new ByteBufOutputStream(this))));
        byte[] bytes = new byte[body.readableBytes()];
        body.readBytes(bytes);
        try {
            out.writeUnsignedVarInt(bytes.length);
            out.write(bytes);
        } catch (IOException ignored) {
        }

    }
}
