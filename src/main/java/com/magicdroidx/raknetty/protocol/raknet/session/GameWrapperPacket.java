package com.magicdroidx.raknetty.protocol.raknet.session;

import com.google.common.io.ByteStreams;
import com.magicdroidx.raknetty.buffer.RakNetByteBuf;
import com.magicdroidx.raknetty.io.RakNetOutputStream;
import com.magicdroidx.raknetty.protocol.game.GamePacket;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class GameWrapperPacket extends SessionPacket {
    public static final int ID = 0xFE;

    public List<GamePacket> packets = new ArrayList<>();

    public GameWrapperPacket() {
        super(GameWrapperPacket.ID);
    }

    @Override
    public void read(RakNetByteBuf in) {
        super.read(in);

        packets.clear();

        InputStream is = new InflaterInputStream(new BufferedInputStream(new ByteBufInputStream(in)));

        try {
            byte[] payload = ByteStreams.toByteArray(is);
            RakNetByteBuf buf = RakNetByteBuf.wrappedBuffer(Unpooled.wrappedBuffer(payload));

            while (buf.isReadable()) {
                int len = buf.readUnsignedVarInt();
                packets.add(GamePacket.from(buf.readBytes(len)));
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void write(RakNetByteBuf out) {
        super.write(out);

        try {
            RakNetOutputStream os = new RakNetOutputStream(new BufferedOutputStream(new DeflaterOutputStream(new ByteBufOutputStream(out))));

            for (GamePacket packet : packets) {

                RakNetByteBuf payload = RakNetByteBuf.buffer();
                packet.write(payload);
                int bodySize = payload.readableBytes();
                os.writeUnsignedVarInt(bodySize);
                os.write(ByteBufUtil.getBytes(payload));
            }
            os.flush();
            os.close();
        } catch (IOException ignored) {
        }
    }

    @Override
    public String toString() {
        return "GameWrapperPacket{" +
                "packets=" + packets +
                '}';
    }
}
