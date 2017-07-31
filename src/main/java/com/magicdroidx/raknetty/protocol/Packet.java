package com.magicdroidx.raknetty.protocol;

import com.magicdroidx.raknetty.buffer.RakNetByteBuf;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */

public interface Packet {
    /**
     * Reads the packet from the given input buffer.
     *
     * @param in The input source to read from.
     */
    void read(RakNetByteBuf in);

    /**
     * Writes the packet to the given output buffer.
     *
     * @param out The output destination to write to.
     */
    void write(RakNetByteBuf out);
}