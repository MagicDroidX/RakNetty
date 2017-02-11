package com.magicdroidx.raknetty.protocol.raknet;

import io.netty.channel.DefaultAddressedEnvelope;

import java.net.InetSocketAddress;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class AddressedRakNetPacket<I extends RakNetPacket> extends DefaultAddressedEnvelope<I, InetSocketAddress> {
    /**
     * Creates a new instance with the specified {@code message}, {@code recipient} address, and
     * {@code sender} address.
     *
     * @param message
     * @param recipient
     * @param sender
     */
    public AddressedRakNetPacket(I message, InetSocketAddress recipient, InetSocketAddress sender) {
        super(message, recipient, sender);
    }

    /**
     * Creates a new instance with the specified {@code message} and {@code recipient} address.
     * The sender address becomes {@code null}.
     *
     * @param message
     * @param recipient
     */
    public AddressedRakNetPacket(I message, InetSocketAddress recipient) {
        super(message, recipient);
    }
}
