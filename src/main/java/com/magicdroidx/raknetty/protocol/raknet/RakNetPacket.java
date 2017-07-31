package com.magicdroidx.raknetty.protocol.raknet;

import com.magicdroidx.raknetty.buffer.RakNetByteBuf;
import com.magicdroidx.raknetty.protocol.Packet;
import com.magicdroidx.raknetty.protocol.raknet.session.*;
import com.magicdroidx.raknetty.protocol.raknet.unconnected.IncompatibleProtocolPacket;
import com.magicdroidx.raknetty.protocol.raknet.unconnected.UnconnectedPingPacket;
import com.magicdroidx.raknetty.protocol.raknet.unconnected.UnconnectedPongPacket;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class RakNetPacket implements Packet {

    private int id;

    public RakNetPacket(int id) {
        this.id = id;
    }

    public static RakNetPacket from(ByteBuf byteBuf) throws IOException {
        RakNetByteBuf in = RakNetByteBuf.wrappedBuffer(byteBuf);

        int id = in.readUnsignedByte();

        RakNetPacket packet;

        if (id >= 0x80 && id <= 0x8f) {
            packet = new FrameSetPacket();
        } else {
            switch (id) {
                case UnconnectedPingPacket.ID:
                    packet = new UnconnectedPingPacket();
                    break;
                case OpenConnectionRequestPacket1.ID:
                    packet = new OpenConnectionRequestPacket1();
                    break;
                case OpenConnectionResponsePacket1.ID:
                    packet = new OpenConnectionResponsePacket1();
                    break;
                case OpenConnectionRequestPacket2.ID:
                    packet = new OpenConnectionRequestPacket2();
                    break;
                case OpenConnectionResponsePacket2.ID:
                    packet = new OpenConnectionResponsePacket2();
                    break;
                case IncompatibleProtocolPacket.ID:
                    packet = new IncompatibleProtocolPacket();
                    break;
                case UnconnectedPongPacket.ID:
                    packet = new UnconnectedPongPacket();
                    break;
                case AcknowledgePacket.ID_ACK:
                    packet = AcknowledgePacket.newACK();
                    break;
                case AcknowledgePacket.ID_NACK:
                    packet = AcknowledgePacket.newNACK();
                    break;
                default:
                    packet = new RawRakNetPacket(id);
                    break;
            }
        }

        packet.read(in);
        return packet;
    }

    public final int id() {
        return id;
    }

    @Override
    public void read(RakNetByteBuf in) {

    }

    @Override
    public void write(RakNetByteBuf out) {
        out.writeByte(id());
    }

    @Override
    public String toString() {
        return "RakNetPacket{" +
                "id=" + id +
                '}';
    }

    public AddressedRakNetPacket envelop(InetSocketAddress recipient) {
        return new AddressedRakNetPacket<>(this, recipient);
    }

    public AddressedRakNetPacket envelop(InetSocketAddress recipient, InetSocketAddress sender) {
        return new AddressedRakNetPacket<>(this, recipient, sender);
    }

    public static class RawRakNetPacket extends RakNetPacket {

        RakNetByteBuf buf;

        public RawRakNetPacket(int id) {
            super(id);
        }

        @Override
        public void read(RakNetByteBuf in) {
            buf = in;
        }

        @Override
        public void write(RakNetByteBuf out) {
            out.writeBytes(buf);
        }

        public RakNetByteBuf buffer() {
            return buf;
        }
    }
}