package com.magicdroidx.raknetty.protocol.raknet.session;

import com.magicdroidx.raknetty.protocol.raknet.RakNetPacket;
import io.netty.buffer.ByteBuf;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class SessionPacket extends RakNetPacket {

    public static SessionPacket from(ByteBuf buf) {
        int id = buf.getByte(0) & 0xff;

        if (id >= 0x80 && id <= 0x8f) {
            throw new IllegalStateException("FrameSetPacket in FramePacket");
        }

        switch (id) {
            case ConnectedPingPacket.ID:
                return new ConnectedPingPacket(buf);
            case ConnectedPongPacket.ID:
                return new ConnectedPongPacket(buf);
            case ConnectionRequestPacket.ID:
                return new ConnectionRequestPacket(buf);
            case ConnectionRequestAcceptedPacket.ID:
                return new ConnectionRequestAcceptedPacket(buf);
            case NewIncomingConnectionPacket.ID:
                return new NewIncomingConnectionPacket(buf);
            case GameWrapperPacket.ID:
                return new GameWrapperPacket(buf);
            default:
                return new SessionPacket(buf);
        }
    }

    public SessionPacket(int id) {
        super(id);
    }

    public SessionPacket(ByteBuf buf) {
        super(buf);
    }
}
