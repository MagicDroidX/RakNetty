package com.magicdroidx.raknetty.io;

import java.io.DataInput;
import java.io.IOException;

/**
 * raknetty Project
 * Author: MagicDroidX
 */

public interface VarIntInput extends DataInput {

    static int decodeZigZag32(final int n) {
        return (n >>> 1) ^ -(n & 1);
    }

    static long decodeZigZag64(final long n) {
        return (n >>> 1) ^ -(n & 1);
    }

    int readVarInt() throws IOException;

    int readUnsignedVarInt() throws IOException;

    long readVarLong() throws IOException;

    long readUnsignedVarLong() throws IOException;
}
