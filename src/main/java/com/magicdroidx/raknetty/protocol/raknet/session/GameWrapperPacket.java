package com.magicdroidx.raknetty.protocol.raknet.session;

import com.magicdroidx.raknetty.buffer.RakNetByteBuf;
import com.magicdroidx.raknetty.io.RakNetInputStream;
import com.magicdroidx.raknetty.io.RakNetOutputStream;
import com.magicdroidx.raknetty.protocol.game.GamePacket;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
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

    @Override
    public void read(RakNetByteBuf in) {
        super.read(in);

        RakNetInputStream is = new RakNetInputStream(new InflaterInputStream(new BufferedInputStream(new ByteBufInputStream(in)), new Inflater(), 64 * 1024));//64KB

        try {
            int bodySize = is.readUnsignedVarInt();
            byte[] bytes = new byte[bodySize];
            is.read(bytes);
            body = GamePacket.from(Unpooled.wrappedBuffer(bytes));
        } catch (Exception ignored) {
        }
    }

    @Override
    public void write(RakNetByteBuf out) {
        super.write(out);

        RakNetOutputStream os = new RakNetOutputStream(new BufferedOutputStream(new DeflaterOutputStream(new ByteBufOutputStream(out))));
        RakNetByteBuf payload = RakNetByteBuf.buffer();
        body.write(payload);
        try {
            int bodySize = payload.readableBytes();
            byte[] bytes = new byte[bodySize];
            payload.readBytes(bytes);
            os.writeUnsignedVarInt(bodySize);
            os.write(bytes);
        } catch (Exception ignored) {
        } finally {
            payload.release();
        }
    }

}
