package com.magicdroidx.raknetty.protocol;

import com.magicdroidx.raknetty.io.VarIntInput;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Packet extends ByteBuf {

    private final ByteBuf buf;

    private final int id;

    public Packet(int id) {
        this(id, null);
    }

    public Packet(ByteBuf buf) {
        this(0, buf);
    }

    private Packet(int id, ByteBuf buf) {
        if (buf == null) {
            this.buf = Unpooled.buffer();
            this.id = id;
        } else {
            this.buf = buf;
            this.id = this.getByte(0) & 0xff;
        }
    }

    public int readVarInt() {
        return VarIntInput.decodeZigZag32(readUnsignedVarInt());
    }

    public int readUnsignedVarInt()  {
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
        return VarIntInput.decodeZigZag64(readUnsignedVarLong());
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

    public int id() {
        return this.id;
    }

    public void decode() {
        this.readerIndex(1);
    }

    public void encode() {
        this.clear();
        this.writeByte(id());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean hasMemoryAddress() {
        return buf.hasMemoryAddress();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final long memoryAddress() {
        return buf.memoryAddress();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int capacity() {
        return buf.capacity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf capacity(int newCapacity) {
        buf.capacity(newCapacity);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int maxCapacity() {
        return buf.maxCapacity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteBufAllocator alloc() {
        return buf.alloc();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteOrder order() {
        //noinspection deprecation
        return buf.order();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf order(ByteOrder endianness) {
        //noinspection deprecation
        return buf.order(endianness);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteBuf unwrap() {
        return buf;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf asReadOnly() {
        return buf.asReadOnly();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReadOnly() {
        return buf.isReadOnly();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isDirect() {
        return buf.isDirect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int readerIndex() {
        return buf.readerIndex();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteBuf readerIndex(int readerIndex) {
        buf.readerIndex(readerIndex);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int writerIndex() {
        return buf.writerIndex();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteBuf writerIndex(int writerIndex) {
        buf.writerIndex(writerIndex);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf setIndex(int readerIndex, int writerIndex) {
        buf.setIndex(readerIndex, writerIndex);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int readableBytes() {
        return buf.readableBytes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int writableBytes() {
        return buf.writableBytes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int maxWritableBytes() {
        return buf.maxWritableBytes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isReadable() {
        return buf.isReadable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isWritable() {
        return buf.isWritable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteBuf clear() {
        buf.clear();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteBuf markReaderIndex() {
        buf.markReaderIndex();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteBuf resetReaderIndex() {
        buf.resetReaderIndex();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteBuf markWriterIndex() {
        buf.markWriterIndex();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteBuf resetWriterIndex() {
        buf.resetWriterIndex();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf discardReadBytes() {
        buf.discardReadBytes();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf discardSomeReadBytes() {
        buf.discardSomeReadBytes();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf ensureWritable(int minWritableBytes) {
        buf.ensureWritable(minWritableBytes);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        return buf.ensureWritable(minWritableBytes, force);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getBoolean(int index) {
        return buf.getBoolean(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte getByte(int index) {
        return buf.getByte(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short getUnsignedByte(int index) {
        return buf.getUnsignedByte(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short getShort(int index) {
        return buf.getShort(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short getShortLE(int index) {
        return buf.getShortLE(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUnsignedShort(int index) {
        return buf.getUnsignedShort(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUnsignedShortLE(int index) {
        return buf.getUnsignedShortLE(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMedium(int index) {
        return buf.getMedium(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMediumLE(int index) {
        return buf.getMediumLE(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUnsignedMedium(int index) {
        return buf.getUnsignedMedium(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUnsignedMediumLE(int index) {
        return buf.getUnsignedMediumLE(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInt(int index) {
        return buf.getInt(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIntLE(int index) {
        return buf.getIntLE(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getUnsignedInt(int index) {
        return buf.getUnsignedInt(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getUnsignedIntLE(int index) {
        return buf.getUnsignedIntLE(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLong(int index) {
        return buf.getLong(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLongLE(int index) {
        return buf.getLongLE(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public char getChar(int index) {
        return buf.getChar(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getFloat(int index) {
        return buf.getFloat(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getDouble(int index) {
        return buf.getDouble(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf getBytes(int index, ByteBuf dst) {
        buf.getBytes(index, dst);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int length) {
        buf.getBytes(index, dst, length);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        buf.getBytes(index, dst, dstIndex, length);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf getBytes(int index, byte[] dst) {
        buf.getBytes(index, dst);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        buf.getBytes(index, dst, dstIndex, length);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        buf.getBytes(index, dst);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        buf.getBytes(index, out, length);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return buf.getBytes(index, out, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        return buf.getBytes(index, out, position, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CharSequence getCharSequence(int index, int length, Charset charset) {
        return buf.getCharSequence(index, length, charset);
    }

    public CharSequence getCharSequence(int index, Charset charset) {
        return getCharSequence(index + 2, getUnsignedShort(index), charset);
    }

    public CharSequence getCharSequence(int index) {
        return getCharSequence(index, Charset.forName("UTF-8"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf setBoolean(int index, boolean value) {
        buf.setBoolean(index, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf setByte(int index, int value) {
        buf.setByte(index, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf setShort(int index, int value) {
        buf.setShort(index, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf setShortLE(int index, int value) {
        buf.setShortLE(index, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf setMedium(int index, int value) {
        buf.setMedium(index, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf setMediumLE(int index, int value) {
        buf.setMediumLE(index, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf setInt(int index, int value) {
        buf.setInt(index, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf setIntLE(int index, int value) {
        buf.setIntLE(index, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf setLong(int index, long value) {
        buf.setLong(index, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf setLongLE(int index, long value) {
        buf.setLongLE(index, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf setChar(int index, int value) {
        buf.setChar(index, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf setFloat(int index, float value) {
        buf.setFloat(index, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf setDouble(int index, double value) {
        buf.setDouble(index, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf setBytes(int index, ByteBuf src) {
        buf.setBytes(index, src);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int length) {
        buf.setBytes(index, src, length);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        buf.setBytes(index, src, srcIndex, length);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf setBytes(int index, byte[] src) {
        buf.setBytes(index, src);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        buf.setBytes(index, src, srcIndex, length);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        buf.setBytes(index, src);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return buf.setBytes(index, in, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        return buf.setBytes(index, in, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        return buf.setBytes(index, in, position, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf setZero(int index, int length) {
        buf.setZero(index, length);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int setCharSequence(int index, CharSequence sequence, Charset charset) {
        buf.setShort(index, sequence.length());
        return buf.setCharSequence(index + 2, sequence, charset) + 2;
    }

    public int setCharSequence(int index, CharSequence sequence) {
        return setCharSequence(index, sequence, Charset.forName("UTF-8"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean readBoolean() {
        return buf.readBoolean();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte readByte() {
        return buf.readByte();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short readUnsignedByte() {
        return buf.readUnsignedByte();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short readShort() {
        return buf.readShort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short readShortLE() {
        return buf.readShortLE();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int readUnsignedShort() {
        return buf.readUnsignedShort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int readUnsignedShortLE() {
        return buf.readUnsignedShortLE();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int readMedium() {
        return buf.readMedium();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int readMediumLE() {
        return buf.readMediumLE();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int readUnsignedMedium() {
        return buf.readUnsignedMedium();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int readUnsignedMediumLE() {
        return buf.readUnsignedMediumLE();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int readInt() {
        return buf.readInt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int readIntLE() {
        return buf.readIntLE();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long readUnsignedInt() {
        return buf.readUnsignedInt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long readUnsignedIntLE() {
        return buf.readUnsignedIntLE();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long readLong() {
        return buf.readLong();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long readLongLE() {
        return buf.readLongLE();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public char readChar() {
        return buf.readChar();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float readFloat() {
        return buf.readFloat();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double readDouble() {
        return buf.readDouble();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf readBytes(int length) {
        return buf.readBytes(length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf readSlice(int length) {
        return buf.readSlice(length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf readRetainedSlice(int length) {
        return buf.readRetainedSlice(length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf readBytes(ByteBuf dst) {
        buf.readBytes(dst);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf readBytes(ByteBuf dst, int length) {
        buf.readBytes(dst, length);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
        buf.readBytes(dst, dstIndex, length);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf readBytes(byte[] dst) {
        buf.readBytes(dst);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        buf.readBytes(dst, dstIndex, length);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf readBytes(ByteBuffer dst) {
        buf.readBytes(dst);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf readBytes(OutputStream out, int length) throws IOException {
        buf.readBytes(out, length);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        return buf.readBytes(out, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int readBytes(FileChannel out, long position, int length) throws IOException {
        return buf.readBytes(out, position, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CharSequence readCharSequence(int length, Charset charset) {
        return buf.readCharSequence(length, charset);
    }

    public CharSequence readCharSequence(Charset charset) {
        return readCharSequence(readUnsignedShort(), charset);
    }

    public CharSequence readCharSequence() {
        return readCharSequence(Charset.forName("UTF-8"));
    }

    public CharSequence readCharSequenceLE(Charset charset) {
        return readCharSequence(readUnsignedShortLE(), charset);
    }

    public CharSequence readCharSequenceLE() {
        return readCharSequenceLE(Charset.forName("UTF-8"));
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
            }
            //TODO: Ipv6
            /*else if (version == 6) {
                byte[] addr = readAddressBytes(16);
                this.skipBytes(10);
                int port = this.readUnsignedShort();
                return new InetSocketAddress(InetAddress.getByAddress(Arrays.copyOfRange(addr, 0, 16)), port);
            }*/
        } catch (UnknownHostException ignored) {

        }
        return null;
    }

    private byte[] readAddressBytes(int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = (byte) ((~this.readByte()) & 0xff);
        }

        return bytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf skipBytes(int length) {
        buf.skipBytes(length);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf writeBoolean(boolean value) {
        buf.writeBoolean(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf writeByte(int value) {
        buf.writeByte(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf writeShort(int value) {
        buf.writeShort(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf writeShortLE(int value) {
        buf.writeShortLE(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf writeMedium(int value) {
        buf.writeMedium(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf writeMediumLE(int value) {
        buf.writeMediumLE(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf writeInt(int value) {
        buf.writeInt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf writeIntLE(int value) {
        buf.writeIntLE(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf writeLong(long value) {
        buf.writeLong(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf writeLongLE(long value) {
        buf.writeLongLE(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf writeChar(int value) {
        buf.writeChar(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf writeFloat(float value) {
        buf.writeFloat(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf writeDouble(double value) {
        buf.writeDouble(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf writeBytes(ByteBuf src) {
        buf.writeBytes(src);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf writeBytes(ByteBuf src, int length) {
        buf.writeBytes(src, length);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
        buf.writeBytes(src, srcIndex, length);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf writeBytes(byte[] src) {
        buf.writeBytes(src);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
        buf.writeBytes(src, srcIndex, length);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf writeBytes(ByteBuffer src) {
        buf.writeBytes(src);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int writeBytes(InputStream in, int length) throws IOException {
        return buf.writeBytes(in, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
        return buf.writeBytes(in, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int writeBytes(FileChannel in, long position, int length) throws IOException {
        return buf.writeBytes(in, position, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf writeZero(int length) {
        buf.writeZero(length);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int writeCharSequence(CharSequence sequence, Charset charset) {
        buf.writeShort(sequence.length());
        return buf.writeCharSequence(sequence, charset) + 2;
    }

    public int writeCharSequence(CharSequence sequence) {
        return writeCharSequence(sequence, Charset.forName("UTF-8"));
    }

    public ByteBuf writeAddress(InetSocketAddress address) {
        byte[] addr = address.getAddress().getAddress();
        if (addr.length == 4) {
            //Ipv4
            this.writeByte(4);
            this.writeBytes(addr);
            this.writeShort(address.getPort());
        }
        //TODO: Ipv6

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        return buf.indexOf(fromIndex, toIndex, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int bytesBefore(byte value) {
        return buf.bytesBefore(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int bytesBefore(int length, byte value) {
        return buf.bytesBefore(length, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int bytesBefore(int index, int length, byte value) {
        return buf.bytesBefore(index, length, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int forEachByte(ByteProcessor processor) {
        return buf.forEachByte(processor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) {
        return buf.forEachByte(index, length, processor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int forEachByteDesc(ByteProcessor processor) {
        return buf.forEachByteDesc(processor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        return buf.forEachByteDesc(index, length, processor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf copy() {
        return buf.copy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf copy(int index, int length) {
        return buf.copy(index, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf slice() {
        return buf.slice();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf retainedSlice() {
        return buf.retainedSlice();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf slice(int index, int length) {
        return buf.slice(index, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf retainedSlice(int index, int length) {
        return buf.retainedSlice(index, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf duplicate() {
        return buf.duplicate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf retainedDuplicate() {
        return buf.retainedDuplicate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int nioBufferCount() {
        return buf.nioBufferCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuffer nioBuffer() {
        return buf.nioBuffer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        return buf.nioBuffer(index, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuffer[] nioBuffers() {
        return buf.nioBuffers();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return buf.nioBuffers(index, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        return buf.internalNioBuffer(index, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasArray() {
        return buf.hasArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] array() {
        return buf.array();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int arrayOffset() {
        return buf.arrayOffset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(Charset charset) {
        return buf.toString(charset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(int index, int length, Charset charset) {
        return buf.toString(index, length, charset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return buf.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) {
        return buf.equals(obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(ByteBuf buffer) {
        return buf.compareTo(buffer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + "(id=" + id() + ", " + buf.toString() + ')';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf retain(int increment) {
        buf.retain(increment);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf retain() {
        buf.retain();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf touch() {
        buf.touch();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuf touch(Object hint) {
        buf.touch(hint);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isReadable(int size) {
        return buf.isReadable(size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isWritable(int size) {
        return buf.isWritable(size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int refCnt() {
        return buf.refCnt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean release() {
        return buf.release();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean release(int decrement) {
        return buf.release(decrement);
    }
}
