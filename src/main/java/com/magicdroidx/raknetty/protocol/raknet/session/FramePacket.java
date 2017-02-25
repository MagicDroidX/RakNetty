package com.magicdroidx.raknetty.protocol.raknet.session;

import com.magicdroidx.raknetty.protocol.Packet;
import com.magicdroidx.raknetty.protocol.raknet.Reliability;
import io.netty.buffer.ByteBuf;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public class FramePacket extends Packet {

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
    public SessionPacket body;

    public FramePacket() {
        super(-1);
    }

    public FramePacket(ByteBuf buf) {
        super(buf);
    }

    public void encodeBody() {
        body.encode();
    }

    @Override
    public void decode() {
        //Frame Packet has no id, do not call super method to move the readerIndex to 1.

        int flags = readUnsignedByte();
        reliability = Reliability.getById((flags & 0b11100000) >> 5);
        fragmented = (flags & 0b00010000) > 0;
        int length = (int) Math.ceil(readUnsignedShort() / 8d);

        if (reliability.isReliable()) {
            indexReliable = readUnsignedMediumLE();
        }

        if (reliability.isSequenced()) {
            indexSequenced = readUnsignedMediumLE();
        }

        if (reliability.isOrdered()) {
            indexOrdered = readUnsignedMediumLE();
            orderChannel = readUnsignedByte();
        }

        if (fragmented) {
            fragmentCount = readInt();
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
            //Notice: We do not encode the body here to get rid of double encoding in most cases.
            //body.encode();
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
            writeMediumLE(indexReliable);
        }

        if (reliability.isSequenced()) {
            writeMediumLE(indexSequenced);
        }

        if (reliability.isOrdered()) {
            writeMediumLE(indexOrdered);
            writeByte(orderChannel);
        }

        if (fragmented) {
            writeInt(fragmentCount);
            writeShort(fragmentID);
            writeInt(fragmentIndex);
        }

        writeBytes(buf);
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
}
