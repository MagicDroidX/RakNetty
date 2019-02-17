package com.magicdroidx.raknetty;

import com.magicdroidx.raknetty.buffer.RakNetByteBuf;
import com.magicdroidx.raknetty.protocol.game.PlayStatusPacket;
import com.magicdroidx.raknetty.protocol.raknet.session.FrameSetPacket;
import com.magicdroidx.raknetty.protocol.raknet.session.GameWrapperPacket;
import com.magicdroidx.raknetty.protocol.raknet.session.SessionPacket;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class Packet {


    public static byte[] hexStringToByteArray(String s) {
        s = s.replace(" ", "");
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    @Test
    public void testEncoding() throws IOException {
        GameWrapperPacket wrapperPacket = new GameWrapperPacket();
        PlayStatusPacket playStatusPacket = new PlayStatusPacket();
        wrapperPacket.packets.add(playStatusPacket);
        RakNetByteBuf buf = RakNetByteBuf.buffer();
        wrapperPacket.write(buf);
        assertEquals(ByteBufUtil.hexDump(buf), "fe789c636562000200002e0008");
    }

    @Test
    public void testDecoding() throws IOException {
        GameWrapperPacket wrapperPacket = new GameWrapperPacket();
        RakNetByteBuf buf = RakNetByteBuf.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{0x78, (byte) 0x9c, 0x63, 0x65, 0x62, 0x00, 0x02, 0x00, 0x00, 0x2e, 0x00, 0x08}));
        wrapperPacket.read(buf);
        assertEquals(wrapperPacket.toString(), "GameWrapperPacket{packets=[PlayStatusPacket{status=0}]}");
    }

    @Test
    public void testFrameSet() throws IOException {
        byte[] data = hexStringToByteArray("09 00 00 60 00 08 08 00 00 02 00 00 00 15");
        FrameSetPacket packet = new FrameSetPacket();
        packet.read(RakNetByteBuf.wrappedBuffer(Unpooled.wrappedBuffer(data)));
        System.out.println(packet);
        SessionPacket p = SessionPacket.from(packet.frames().get(0).body);
        System.out.println(p);
    }
}
