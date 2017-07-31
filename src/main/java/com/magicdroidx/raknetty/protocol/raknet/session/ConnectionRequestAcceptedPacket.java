package com.magicdroidx.raknetty.protocol.raknet.session;

import com.magicdroidx.raknetty.buffer.RakNetByteBuf;

import java.net.InetSocketAddress;
import java.util.Arrays;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public final class ConnectionRequestAcceptedPacket extends SessionPacket {
    public static final int ID = 0x10;

    public InetSocketAddress clientAddress;
    //public int systemIndex;
    public InetSocketAddress[] addresses = new InetSocketAddress[20];
    public long incomingTimestamp;
    public long serverTimestamp;

    public ConnectionRequestAcceptedPacket() {
        super(ConnectionRequestAcceptedPacket.ID);
        Arrays.fill(addresses, new InetSocketAddress("255.255.255.255", 19132));
    }

    @Override
    public void read(RakNetByteBuf in) {
        super.read(in);
        clientAddress = in.readAddress();
        int systemIndex = in.readUnsignedShort();
        for (int i = 0; i < systemIndex; i++) {
            addresses[i] = in.readAddress();
        }
        incomingTimestamp = in.readLong();
        serverTimestamp = in.readLong();
    }

    @Override
    public void write(RakNetByteBuf out) {
        System.out.println(toString());
        super.write(out);
        out.writeAddress(clientAddress);
        int systemIndex = addresses.length;
        out.writeShort(systemIndex);
        for (int i = 0; i < systemIndex; i++) {
            out.writeAddress(addresses[i]);
        }
        out.writeLong(incomingTimestamp);
        out.writeLong(serverTimestamp);
    }

    @Override
    public String toString() {
        return "ConnectionRequestAcceptedPacket{" +
                "clientAddress=" + clientAddress +
                ", addresses=" + Arrays.toString(addresses) +
                ", incomingTimestamp=" + incomingTimestamp +
                ", serverTimestamp=" + serverTimestamp +
                '}';
    }
}
