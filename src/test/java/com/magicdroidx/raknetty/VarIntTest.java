package com.magicdroidx.raknetty;

import com.magicdroidx.raknetty.io.RakNetInputStream;
import com.magicdroidx.raknetty.io.RakNetOutputStream;
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
            RakNetOutputStream out = new RakNetOutputStream(baos);
            out.writeUnsignedVarInt(i);
            byte[] bytes = baos.toByteArray();
            RakNetInputStream in = new RakNetInputStream(new ByteArrayInputStream(bytes));
            int result = in.readUnsignedVarInt();
            assertEquals(i, result);
        }
    }

    @Test
    public void testVarInt() throws IOException {
        int[] ints = new int[]{1, 129, 257, 513, 1025, 2049};
        for (int i : ints) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            RakNetOutputStream out = new RakNetOutputStream(baos);
            out.writeVarInt(i);
            byte[] bytes = baos.toByteArray();
            RakNetInputStream in = new RakNetInputStream(new ByteArrayInputStream(bytes));
            int result = in.readVarInt();
            assertEquals(i, result);
        }
    }

    @Test
    public void testUnsignedVarLong() throws IOException {
        long[] longs = new long[]{123456789123L, 456789123456L, 98765432123L};
        for (long l : longs) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            RakNetOutputStream out = new RakNetOutputStream(baos);
            out.writeUnsignedVarLong(l);
            byte[] bytes = baos.toByteArray();
            RakNetInputStream in = new RakNetInputStream(new ByteArrayInputStream(bytes));
            long result = in.readUnsignedVarLong();
            assertEquals(l, result);
        }
    }

    @Test
    public void testVarLong() throws IOException {
        long[] longs = new long[]{123456789123L, 456789123456L, 98765432123L};
        for (long l : longs) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            RakNetOutputStream out = new RakNetOutputStream(baos);
            out.writeVarLong(l);
            byte[] bytes = baos.toByteArray();
            RakNetInputStream in = new RakNetInputStream(new ByteArrayInputStream(bytes));
            long result = in.readVarLong();
            assertEquals(l, result);
        }
    }
}
