package com.magicdroidx.raknetty.protocol.raknet;

import com.magicdroidx.raknetty.protocol.Packet;
import com.magicdroidx.raknetty.protocol.raknet.session.*;
import com.magicdroidx.raknetty.protocol.raknet.unconnected.IncompatibleProtocolPacket;
import com.magicdroidx.raknetty.protocol.raknet.unconnected.UnconnectedPingPacket;
import com.magicdroidx.raknetty.protocol.raknet.unconnected.UnconnectedPongPacket;
import io.netty.buffer.ByteBuf;

import java.net.InetSocketAddress;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class RakNetPacket extends Packet {

    public static RakNetPacket from(ByteBuf buf) {
        int id = buf.getByte(0) & 0xff;
        if (id >= 0x80 && id <= 0x8f) {
            return new FrameSetPacket(buf);
        }

        switch (id) {
            case UnconnectedPingPacket.ID:
                return new UnconnectedPingPacket(buf);
            case OpenConnectionRequestPacket1.ID:
                return new OpenConnectionRequestPacket1(buf);
            case OpenConnectionResponsePacket1.ID:
                return new OpenConnectionResponsePacket1(buf);
            case OpenConnectionRequestPacket2.ID:
                return new OpenConnectionRequestPacket2(buf);
            case OpenConnectionResponsePacket2.ID:
                return new OpenConnectionResponsePacket2(buf);
            case IncompatibleProtocolPacket.ID:
                return new IncompatibleProtocolPacket(buf);
            case UnconnectedPongPacket.ID:
                return new UnconnectedPongPacket(buf);
            case AcknowledgePacket.ID_ACK:
            case AcknowledgePacket.ID_NACK:
                return AcknowledgePacket.from(buf);
            default:
                return new RakNetPacket(buf);
        }
    }

    public RakNetPacket(int id) {
        super(id);
    }

    public RakNetPacket(ByteBuf buf) {
        super(buf);
    }

    public AddressedRakNetPacket envelop(InetSocketAddress recipient) {
        return new AddressedRakNetPacket<>(this, recipient);
    }

    public AddressedRakNetPacket envelop(InetSocketAddress recipient, InetSocketAddress sender) {
        return new AddressedRakNetPacket<>(this, recipient, sender);
    }
}