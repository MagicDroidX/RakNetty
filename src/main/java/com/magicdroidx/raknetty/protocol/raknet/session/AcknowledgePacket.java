package com.magicdroidx.raknetty.protocol.raknet.session;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public final class AcknowledgePacket extends SessionPacket implements FramelessPacket {
    public static final int ID_ACK = 0xc0;
    public static final int ID_NACK = 0xa0;

    public Set<Integer> records = new HashSet<>();

    private AcknowledgePacket(int id) {
        super(id);
    }

    private AcknowledgePacket(ByteBuf buf) {
        super(buf);
    }

    public Set<Integer> records() {
        return records;
    }

    @Override
    public void decode() {
        super.decode();
        records = new HashSet<>();
        int count = readUnsignedShort();
        for (int i = 0; i < count; i++) {
            boolean isRange = !readBoolean(); //Notice: 0 for range, 1 for no range
            if (!isRange) {
                records.add(readUnsignedMediumLE());
            } else {
                int startIndex = readUnsignedMediumLE();
                int endIndex = readUnsignedMediumLE();
                for (int index = startIndex; index <= endIndex; index++) {
                    records.add(index);
                }
            }
        }
    }

    @Override
    public void encode() {
        super.encode();
        ByteBuf buf = Unpooled.buffer();

        int recodeCnt = 0;

        Integer[] records = this.records.toArray(new Integer[0]);
        Arrays.sort(records);

        for (int i = 0; i < records.length; i++) {
            int indexStart = records[i];
            int indexEnd = indexStart;

            while (i + 1 < records.length && records[i + 1] == (indexEnd + 1)) {
                indexEnd++;
                i++;
            }

            if (indexStart == indexEnd) {
                buf.writeBoolean(true); //No range
                buf.writeMediumLE(indexStart);
                recodeCnt++;
            } else {
                buf.writeBoolean(false); //Range
                buf.writeMediumLE(indexStart);
                buf.writeMediumLE(indexEnd);
                recodeCnt++;
            }
        }

        writeShort(recodeCnt);
        writeBytes(buf);
    }

    public boolean isACK() {
        return id() == ID_ACK;
    }

    public boolean isNACK() {
        return id() == ID_NACK;
    }

    public static AcknowledgePacket newACK() {
        return new AcknowledgePacket(ID_ACK);
    }

    public static AcknowledgePacket newNACK() {
        return new AcknowledgePacket(ID_NACK);
    }

    public static AcknowledgePacket from(ByteBuf buf) {
        switch (buf.getByte(0) & 0xff) {
            case ID_ACK:
            case ID_NACK:
                return new AcknowledgePacket(buf);
            default:
                return null;
        }
    }
}
