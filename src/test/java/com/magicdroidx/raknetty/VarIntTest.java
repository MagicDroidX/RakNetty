package com.magicdroidx.raknetty;

import com.magicdroidx.raknetty.io.VarIntInputStream;
import com.magicdroidx.raknetty.io.VarIntOutputStream;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class VarIntTest {


    @Test
    public void testUnsignedVarInt() throws IOException {
        int[] ints = new int[]{1, 129, 257, 513, 1025, 2049};
        for (int i : ints) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            VarIntOutputStream out = new VarIntOutputStream(baos);
            out.writeUnsignedVarInt(i);
            byte[] bytes = baos.toByteArray();
            VarIntInputStream in = new VarIntInputStream(new ByteArrayInputStream(bytes));
            int result = in.readUnsignedVarInt();
            assertEquals(i, result);
        }
    }

    @Test
    public void testVarInt() throws IOException {
        int[] ints = new int[]{1, 129, 257, 513, 1025, 2049};
        for (int i : ints) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            VarIntOutputStream out = new VarIntOutputStream(baos);
            out.writeVarInt(i);
            byte[] bytes = baos.toByteArray();
            VarIntInputStream in = new VarIntInputStream(new ByteArrayInputStream(bytes));
            int result = in.readVarInt();
            assertEquals(i, result);
        }
    }

    @Test
    public void testUnsignedVarLong() throws IOException {
        long[] longs = new long[]{123456789123L, 456789123456L, 98765432123L};
        for (long l : longs) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            VarIntOutputStream out = new VarIntOutputStream(baos);
            out.writeUnsignedVarLong(l);
            byte[] bytes = baos.toByteArray();
            VarIntInputStream in = new VarIntInputStream(new ByteArrayInputStream(bytes));
            long result = in.readUnsignedVarLong();
            assertEquals(l, result);
        }
    }

    @Test
    public void testVarLong() throws IOException {
        long[] longs = new long[]{123456789123L, 456789123456L, 98765432123L};
        for (long l : longs) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            VarIntOutputStream out = new VarIntOutputStream(baos);
            out.writeVarLong(l);
            byte[] bytes = baos.toByteArray();
            VarIntInputStream in = new VarIntInputStream(new ByteArrayInputStream(bytes));
            long result = in.readVarLong();
            assertEquals(l, result);
        }
    }
}
