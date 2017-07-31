package com.magicdroidx.raknetty.io;

import java.io.DataOutput;
import java.io.IOException;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public interface RakNetOutput extends DataOutput {

    public static int encodeZigZag32(final int n) {
        return (n << 1) ^ (n >> 31);
    }

    public static long encodeZigZag64(final long n) {
        return (n << 1) ^ (n >> 63);
    }

    void writeVarInt(int value) throws IOException;

    void writeUnsignedVarInt(int value) throws IOException;

    void writeVarLong(long value) throws IOException;

    void writeUnsignedVarLong(long value) throws IOException;
}
