package com.magicdroidx.raknetty.protocol.raknet.session;

import io.netty.buffer.ByteBuf;

import java.net.InetSocketAddress;
import java.util.Arrays;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public final class NewIncomingConnectionPacket extends SessionPacket {
    public static final int ID = 0x13;

    public InetSocketAddress clientAddress;
    public InetSocketAddress[] addresses = new InetSocketAddress[10];
    public long incomingTimestamp;
    public long serverTimestamp;

    public NewIncomingConnectionPacket() {
        super(NewIncomingConnectionPacket.ID);
        Arrays.fill(addresses, new InetSocketAddress("255.255.255.255", 19132));
    }

    public NewIncomingConnectionPacket(ByteBuf buf) {
        super(buf);
        Arrays.fill(addresses, new InetSocketAddress("255.255.255.255", 19132));
    }

    @Override
    public void decode() {
        super.decode();
        clientAddress = readAddress();
        for (int i = 0; i < 10; i++) {
            addresses[i] = readAddress();
        }
        incomingTimestamp = readLong();
        serverTimestamp = readLong();
    }

    @Override
    public void encode() {
        super.encode();
        writeAddress(clientAddress);
        for (int i = 0; i < 10; i++) {
            writeAddress(addresses[i]);
        }
        writeLong(incomingTimestamp);
        writeLong(serverTimestamp);
    }
}
