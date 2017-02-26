package com.magicdroidx.raknetty.protocol.raknet.session;

import io.netty.buffer.ByteBuf;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public final class DisconnectionNotificationPacket extends SessionPacket {
    public static final int ID = 0x15;

    public DisconnectionNotificationPacket() {
        super(DisconnectionNotificationPacket.ID);
    }

    public DisconnectionNotificationPacket(ByteBuf buf) {
        super(buf);
    }
}
