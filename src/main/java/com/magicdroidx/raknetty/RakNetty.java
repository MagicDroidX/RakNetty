package com.magicdroidx.raknetty;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public class RakNetty {

    public static final byte[] OFFLINE_MESSAGE_ID = new byte[]{
            (byte) 0x00, (byte) 0xff, (byte) 0xff, (byte) 0x00,
            (byte) 0xfe, (byte) 0xfe, (byte) 0xfe, (byte) 0xfe,
            (byte) 0xfd, (byte) 0xfd, (byte) 0xfd, (byte) 0xfd,
            (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78
    };

    public static final int PROTOCOL_VERSION = 8;

    public static void main(String[] args) throws Exception {
        RakNetServer server = new RakNetServer();
        server.run();
    }
}
