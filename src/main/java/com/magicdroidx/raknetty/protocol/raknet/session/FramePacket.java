package com.magicdroidx.raknetty.protocol.raknet.session;

import com.magicdroidx.raknetty.buffer.RakNetByteBuf;
import com.magicdroidx.raknetty.protocol.Packet;
import com.magicdroidx.raknetty.protocol.raknet.Reliability;
import io.netty.buffer.ByteBuf;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public class FramePacket implements Packet {

    public static final int OVERHEAD_LENGTH = 1 + 2; //flags and length
    public static final int FRAGMENT_OVERHEAD_LENGTH = 4 + 2 + 4; //fragmentCount, fragmentID and fragmentIndex

    public Reliability reliability;
    public boolean fragmented;

    //Only if reliable
    public int indexReliable;

    //Only if sequenced
    public int indexSequenced;

    //Only if ordered
    public int indexOrdered;
    public int orderChannel;

    //Only if fragmented
    public int fragmentCount;
    public int fragmentID;
    public int fragmentIndex;
    public ByteBuf fragment;

    //Only if not fragmented
    public ByteBuf body;

    @Override
    public void read(RakNetByteBuf in) {
        int flags = in.readUnsignedByte();
        reliability = Reliability.getById((flags & 0b11100000) >> 5);
        fragmented = (flags & 0b00010000) > 0;
        int length = (int) Math.ceil(in.readUnsignedShort() / 8d);

        if (reliability.isReliable()) {
            indexReliable = in.readUnsignedMediumLE();
        }

        if (reliability.isSequenced()) {
            indexSequenced = in.readUnsignedMediumLE();
        }

        if (reliability.isOrdered()) {
            indexOrdered = in.readUnsignedMediumLE();
            orderChannel = in.readUnsignedByte();
        }

        if (fragmented) {
            fragmentCount = in.readInt();
            fragmentID = in.readUnsignedShort();
            fragmentIndex = in.readInt();
            fragment = in.readBytes(length);
        } else {
            body = in.readBytes(length);
        }
    }

    @Override
    public void write(RakNetByteBuf out) {
        ByteBuf buf;
        if (fragmented) {
            buf = fragment;
        } else {
            buf = body;
        }

        int flags = reliability.id() << 5;
        if (fragmented) {
            flags |= 0b00010000;
        }
        out.writeByte(flags);
        out.writeShort(buf.writerIndex() * 8);

        if (reliability.isReliable()) {
            out.writeMediumLE(indexReliable);
        }

        if (reliability.isSequenced()) {
            out.writeMediumLE(indexSequenced);
        }

        if (reliability.isOrdered()) {
            out.writeMediumLE(indexOrdered);
            out.writeByte(orderChannel);
        }

        if (fragmented) {
            out.writeInt(fragmentCount);
            out.writeShort(fragmentID);
            out.writeInt(fragmentIndex);
        }

        out.writeBytes(buf);
        buf.release();
    }

    public int length() {
        int length = OVERHEAD_LENGTH;

        length += reliability.length();

        if (fragmented) {
            length += 4;
            length += 2;
            length += 4;
            length += fragment.writerIndex();
        } else {
            length += body.writerIndex();
        }

        return length;
    }

    public void release() {
        if (fragment != null) {
            fragment.release();
        }

        if (body != null) {
            body.release();
        }
    }

    @Override
    public String toString() {
        return "FramePacket{" +
                "reliability=" + reliability +
                ", fragmented=" + fragmented +
                ", indexReliable=" + indexReliable +
                ", indexSequenced=" + indexSequenced +
                ", indexOrdered=" + indexOrdered +
                ", orderChannel=" + orderChannel +
                ", fragmentCount=" + fragmentCount +
                ", fragmentID=" + fragmentID +
                ", fragmentIndex=" + fragmentIndex +
                ", fragment=" + fragment +
                ", body=" + body +
                '}';
    }
}
