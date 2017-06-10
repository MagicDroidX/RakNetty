package com.magicdroidx.raknetty.protocol.raknet.session;

import com.google.common.io.ByteStreams;
import com.magicdroidx.raknetty.io.VarIntInputStream;
import com.magicdroidx.raknetty.protocol.game.GamePacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterInputStream;
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
        System.out.println("Decoding");
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
        //writeBytes(body);
        InputStream in = new DeflaterInputStream(new ByteBufInputStream(body));
        OutputStream out = new ByteBufOutputStream(this);
        try {
            int numberOfBytes = (int) ByteStreams.copy(in, out);
            writerIndex(readerIndex() + numberOfBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
