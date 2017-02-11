package com.magicdroidx.raknetty.protocol.raknet.session;

import com.magicdroidx.raknetty.protocol.Packet;
import com.magicdroidx.raknetty.protocol.raknet.Reliability;
import io.netty.buffer.ByteBuf;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public class FramePacket extends Packet {
    public Reliability reliability;
    public boolean fragmented;

    //Only if reliable
    public int frameIndexReliable;

    //Only if sequenced
    public int frameIndexSequenced;

    //Only if ordered
    public int frameIndexOrdered;
    public int orderChannel;

    //Only if fragmented
    public int fragmentSize;
    public int fragmentID;
    public int fragmentIndex;
    public ByteBuf fragment;

    //Only if not fragmented
    public SessionPacket body;

    public FramePacket() {
        super(-1);
    }

    public FramePacket(ByteBuf buf) {
        super(buf);
    }

    @Override
    public void decode() {
        //Frame Packet has no id, do not call super method to move the readerIndex to 1.

        int flags = readUnsignedByte();
        reliability = Reliability.getById((flags & 0b11100000) >> 5);
        fragmented = (flags & 0b00010000) > 0;
        int length = (int) Math.ceil(readUnsignedShort() / 8d);

        if (reliability.isReliable()) {
            frameIndexReliable = readUnsignedMediumLE();
        }

        if (reliability.isSequenced()) {
            frameIndexSequenced = readUnsignedMediumLE();
        }

        if (reliability.isOrdered()) {
            frameIndexOrdered = readUnsignedMediumLE();
            orderChannel = readUnsignedByte();
        }

        if (fragmented) {
            fragmentSize = readInt();
            fragmentID = readUnsignedShort();
            fragmentIndex = readInt();
            fragment = readBytes(length);
        } else {
            body = SessionPacket.from(readBytes(length));
            body.decode();
        }
    }

    @Override
    public void encode() {
        this.clear();
        //Frame Packet has no id, do not call super method to write an packet id.

        ByteBuf buf;
        if (fragmented) {
            buf = fragment;
        } else {
            body.encode();
            buf = body;
        }
        buf = buf.copy(0, buf.writerIndex());

        int flags = reliability.id() << 5;
        if (fragmented) {
            flags |= 0b00010000;
        }
        writeByte(flags);
        writeShort(buf.writerIndex() * 8);

        if (reliability.isReliable()) {
            writeMediumLE(frameIndexReliable);
        }

        if (reliability.isSequenced()) {
            writeMediumLE(frameIndexSequenced);
        }

        if (reliability.isOrdered()) {
            writeMediumLE(frameIndexOrdered);
            writeByte(orderChannel);
        }

        if (fragmented) {
            writeInt(fragmentSize);
            writeShort(fragmentID);
            writeInt(fragmentIndex);
        }

        writeBytes(buf);
        buf.release();
    }

    public int length() {
        int length = 1 + 2; //flags and length

        if (reliability.isReliable()) {
            length += 3;
        }

        if (reliability.isSequenced()) {
            length += 3;
        }

        if (reliability.isOrdered()) {
            length += 3;
            length += 1;
        }

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
}
