package com.magicdroidx.raknetty.protocol.game;

import com.magicdroidx.raknetty.buffer.RakNetByteBuf;
import io.netty.buffer.ByteBufUtil;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class DisconnectPacket extends GamePacket {
    public static final int ID = 0x05;

    boolean hideReason = false;
    CharSequence reason = "NONE";

    public DisconnectPacket() {
        super(DisconnectPacket.ID);
    }

    @Override
    public void read(RakNetByteBuf in) {
        super.read(in);
        hideReason = in.readBoolean();
        if (!hideReason) {
            reason = in.readString();
        }
    }

    @Override
    public void write(RakNetByteBuf out) {
        super.write(out);
        out.writeBoolean(hideReason);
        out.writeString(reason);
    }

}
