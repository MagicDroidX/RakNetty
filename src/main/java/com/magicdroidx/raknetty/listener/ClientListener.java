package com.magicdroidx.raknetty.listener;

import com.magicdroidx.raknetty.handler.session.Session;
import com.magicdroidx.raknetty.protocol.game.GamePacket;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public interface ClientListener {
    void registered();

    void connected();

    void packetReceived(GamePacket packet);

    void disconnected();
}
