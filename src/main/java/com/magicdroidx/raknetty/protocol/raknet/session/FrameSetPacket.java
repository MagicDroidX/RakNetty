package com.magicdroidx.raknetty.protocol.raknet.session;

import com.magicdroidx.raknetty.buffer.RakNetByteBuf;

import java.util.ArrayList;
import java.util.List;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public final class FrameSetPacket extends SessionPacket {
    public static final int ID = 0x84; //Notice: It should be 0x80 to 0x8d, but the client often uses 0x84 and it works.

    public static int OVERHEAD_LENGTH = 1 + 3; //ID and Frame Index

    public int index;

    private List<FramePacket> frames = new ArrayList<>();

    public FrameSetPacket() {
        super(FrameSetPacket.ID);
    }

    public List<FramePacket> frames() {
        return frames;
    }

    @Override
    public void read(RakNetByteBuf in) {
        super.read(in);
        index = in.readUnsignedMediumLE();
        while (in.isReadable()) {
            FramePacket frame = new FramePacket();
            frame.read(in);
            this.frames.add(frame);
        }
    }

    @Override
    public void write(RakNetByteBuf out) {
        super.write(out);
        out.writeMediumLE(index);
        for (FramePacket frame : frames) {
            frame.write(out);
        }
    }

    public int length() {
        int length = OVERHEAD_LENGTH;
        for (FramePacket frame : frames) {
            length += frame.length();
        }

        return length;
    }

    @Override
    public String toString() {
        return "FrameSetPacket{" +
                "index=" + index +
                ", frames=" + frames +
                '}';
    }
}
