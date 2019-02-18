package com.magicdroidx.raknetty.protocol.raknet.session;

import com.magicdroidx.raknetty.buffer.RakNetByteBuf;
import com.magicdroidx.raknetty.protocol.raknet.RakNetPacket;
import io.netty.buffer.ByteBuf;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class SessionPacket extends RakNetPacket {

    public SessionPacket(int id) {
        super(id);
    }

    public static SessionPacket from(ByteBuf buf) {

        RakNetByteBuf in = RakNetByteBuf.wrappedBuffer(buf);

        int id = in.readByte() & 0xff;

        if (id >= 0x80 && id <= 0x8f) {
            throw new IllegalStateException("FrameSetPacket in FramePacket");
        }

        SessionPacket packet;

        switch (id) {
            case ConnectedPingPacket.ID:
                packet = new ConnectedPingPacket();
                break;
            case ConnectedPongPacket.ID:
                packet = new ConnectedPongPacket();
                break;
            case ConnectionRequestPacket.ID:
                packet = new ConnectionRequestPacket();
                break;
            case ConnectionRequestAcceptedPacket.ID:
                packet = new ConnectionRequestAcceptedPacket();
                break;
            case NewIncomingConnectionPacket.ID:
                packet = new NewIncomingConnectionPacket();
                break;
            case DisconnectionNotificationPacket.ID:
                packet = new DisconnectionNotificationPacket();
                break;
            case GameWrapperPacket.ID:
                packet = new GameWrapperPacket();
                break;

            default:
                return new SessionPacket(id);
        }

        packet.read(in);
        in.release();
        return packet;
    }
}
