package com.magicdroidx.raknetty.protocol.raknet.session;

import io.netty.buffer.ByteBuf;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public final class DisconnectPacket extends SessionPacket {
    public static final int ID = 0x15;

    public DisconnectPacket() {
        super(DisconnectPacket.ID);
    }

    public DisconnectPacket(ByteBuf buf) {
        super(buf);
    }
}
