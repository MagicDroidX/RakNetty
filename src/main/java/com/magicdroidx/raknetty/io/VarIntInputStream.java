package com.magicdroidx.raknetty.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class VarIntInputStream extends DataInputStream implements VarIntInput {

    public VarIntInputStream(InputStream in) {
        super(in);
    }

    @Override
    public int readVarInt() throws IOException {
        return VarIntInput.decodeZigZag32(readUnsignedVarInt());
    }

    @Override
    public int readUnsignedVarInt() throws IOException {
        int value = 0;
        int size = 0;
        int b;

        while (((b = this.readByte()) & 0x80) == 0x80) {
            value |= (b & 0x7F) << (size++ * 7);
            if (size > 5) {
                throw new IOException("VarInt too big");
            }
        }

        return value | ((b & 0x7F) << (size * 7));
    }

    @Override
    public long readVarLong() throws IOException {
        return VarIntInput.decodeZigZag64(readUnsignedVarLong());
    }

    @Override
    public long readUnsignedVarLong() throws IOException {
        long value = 0;
        int size = 0;
        int b;
        while (((b = this.readByte()) & 0x80) == 0x80) {
            value |= (long) (b & 0x7F) << (size++ * 7);
            if (size > 10) {
                throw new IOException("VarLong too big");
            }
        }

        return value | ((long) (b & 0x7F) << (size * 7));
    }
}
