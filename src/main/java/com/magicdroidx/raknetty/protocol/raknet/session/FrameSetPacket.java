package com.magicdroidx.raknetty.protocol.raknet.session;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public final class FrameSetPacket extends SessionPacket {
    public static final int ID = 0x84; //Notice: It should be 0x80 to 0x8d, but the client often uses 0x84 and it works.

    public int index;
    public List<FramePacket> frames = new ArrayList<>();

    public FrameSetPacket() {
        super(FrameSetPacket.ID);
    }

    public FrameSetPacket(ByteBuf buf) {
        super(buf);
    }

    public List<FramePacket> frames() {
        return frames;
    }

    @Override
    public void decode() {
        super.decode();
        index = readUnsignedMediumLE();
        while (isReadable()) {
            FramePacket frame = new FramePacket(copy());
            frame.decode();
            readerIndex(readerIndex() + frame.readerIndex()); //Move the readerIndex
            //TODO: Check if it's necessary: if (frame.body.writerIndex() == 0) break;
            this.frames.add(frame);
        }
    }

    @Override
    public void encode() {
        super.encode();
        writeMediumLE(index);
        for (FramePacket frame : frames) {
            frame.encode();
            writeBytes(frame.copy(0, frame.writerIndex()));
        }
    }

    public int length() {
        int length = 1 + 3; //ID and Frame Index
        for (FramePacket frame : frames) {
            length += frame.length();
        }

        return length;
    }
}
