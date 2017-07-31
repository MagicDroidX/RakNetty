package com.magicdroidx.raknetty.protocol.raknet.session;

import com.magicdroidx.raknetty.buffer.RakNetByteBuf;

import java.net.InetSocketAddress;
import java.util.Arrays;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public final class NewIncomingConnectionPacket extends SessionPacket {
    public static final int ID = 0x13;

    public InetSocketAddress serverAddress;
    public InetSocketAddress[] addresses = new InetSocketAddress[20];
    public long incomingTimestamp;
    public long serverTimestamp;

    public NewIncomingConnectionPacket() {
        super(NewIncomingConnectionPacket.ID);
        Arrays.fill(addresses, new InetSocketAddress("255.255.255.255", 19132));
    }

    @Override
    public void read(RakNetByteBuf in) {
        super.read(in);
        serverAddress = in.readAddress();
        for (int i = 0; i < 20; i++) {
            addresses[i] = in.readAddress();
        }
        incomingTimestamp = in.readLong();
        serverTimestamp = in.readLong();
    }

    @Override
    public void write(RakNetByteBuf out) {
        super.write(out);
        out.writeAddress(serverAddress);
        for (int i = 0; i < 20; i++) {
            out.writeAddress(addresses[i]);
        }
        out.writeLong(incomingTimestamp);
        out.writeLong(serverTimestamp);
    }

    @Override
    public String toString() {
        return "NewIncomingConnectionPacket{" +
                "serverAddress=" + serverAddress +
                ", addresses=" + Arrays.toString(addresses) +
                ", incomingTimestamp=" + incomingTimestamp +
                ", serverTimestamp=" + serverTimestamp +
                '}';
    }
}
