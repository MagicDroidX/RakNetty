package com.magicdroidx.raknetty.buffer;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class RakNetByteBuf extends WrappedBuf {

    RakNetByteBuf(ByteBuf buf) {
        super(buf);
    }

    static int decodeZigZag32(final int n) {
        return (n >>> 1) ^ -(n & 1);
    }

    static long decodeZigZag64(final long n) {
        return (n >>> 1) ^ -(n & 1);
    }

    static int encodeZigZag32(final int n) {
        return (n << 1) ^ (n >> 31);
    }

    static long encodeZigZag64(final long n) {
        return (n << 1) ^ (n >> 63);
    }

    public static RakNetByteBuf buffer() {
        return new RakNetByteBuf(Unpooled.buffer());
    }

    public static RakNetByteBuf wrappedBuffer(ByteBuf buf) {
        if (buf instanceof RakNetByteBuf) {
            return (RakNetByteBuf) buf;
        }

        return new RakNetByteBuf(buf);
    }

    public int readVarInt() {
        return decodeZigZag32(readUnsignedVarInt());
    }

    public int readUnsignedVarInt() {
        int value = 0;
        int size = 0;
        int b;
        while (((b = this.readByte()) & 0x80) == 0x80) {
            value |= (b & 0x7F) << (size++ * 7);
            if (size > 5) {
                throw new IllegalStateException("VarInt too big");
            }
        }

        return value | ((b & 0x7F) << (size * 7));
    }

    public long readVarLong() {
        return decodeZigZag64(readUnsignedVarLong());
    }

    public long readUnsignedVarLong() {
        long value = 0;
        int size = 0;
        int b;
        while (((b = this.readByte()) & 0x80) == 0x80) {
            value |= (long) (b & 0x7F) << (size++ * 7);
            if (size > 10) {
                throw new IllegalStateException("VarLong too big");
            }
        }

        return value | ((long) (b & 0x7F) << (size * 7));
    }

    private byte[] readAddressBytes(int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = (byte) ((~this.readByte()) & 0xff);
        }

        return bytes;
    }

    /**
     * Reads an IPv4/IPv6 address
     *
     * @return An IPv4/IPv6 address
     */
    public InetSocketAddress readAddress() {
        int version = this.readUnsignedByte();
        try {
            if (version == 4) {
                byte[] addr = readAddressBytes(4);
                int port = this.readUnsignedShort();
                return new InetSocketAddress(InetAddress.getByAddress(addr), port);
            } else if (version == 6) {
                readShort(); // Address family
                int port = this.readUnsignedShort();
                readLong(); // Flow info
                byte[] addr = readAddressBytes(16);
                return new InetSocketAddress(InetAddress.getByAddress(addr), port);
            }
        } catch (UnknownHostException ignored) {

        }
        return null;
    }

    public CharSequence readString() {
        return readCharSequence(readUnsignedVarInt(), Charsets.UTF_8);
    }

    public CharSequence readFixedString() {
        return readCharSequence(readUnsignedShort(), Charsets.UTF_8);
    }

    public void writeVarInt(int value) {
        writeUnsignedVarInt(encodeZigZag32(value));
    }

    public void writeUnsignedVarInt(int value) {
        while (true) {
            if ((value & ~0x7F) == 0) {
                writeByte(value);
                return;
            } else {
                writeByte((value & 0x7F) | 0x80);
                value >>>= 7;
            }
        }
    }

    public void writeVarLong(long value) {
        writeUnsignedVarLong(encodeZigZag64(value));
    }

    public void writeUnsignedVarLong(long value) {
        while (true) {
            if ((value & ~0x7FL) == 0) {
                writeByte((int) value);
                return;
            } else {
                writeByte(((int) value & 0x7F) | 0x80);
                value >>>= 7;
            }
        }
    }

    public ByteBuf writeAddress(InetSocketAddress address) {
        byte[] addr = address.getAddress().getAddress();
        if (addr.length == 4) {
            //IPv4
            this.writeByte(4);
            this.writeBytes(addr);
            this.writeShort(address.getPort());
        } else if (addr.length == 16) {
            //TODO: Test required
            //IPv6
            this.writeShort(10); //AF_INET6 10
            this.writeShort(address.getPort());
            this.writeLong(0xfffff); //IPV6_FLOWINFO_FLOW_LABEL
            this.writeBytes(addr);
        }

        return this;
    }

    public ByteBuf writeString(CharSequence sequence) {
        writeUnsignedVarInt(sequence.length());
        writeCharSequence(sequence, Charsets.UTF_8);
        return this;
    }

    public ByteBuf writeFixedString(CharSequence sequence) {
        writeShort(sequence.length());
        writeCharSequence(sequence, Charsets.UTF_8);
        return this;
    }
}
