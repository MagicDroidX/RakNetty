package com.magicdroidx.raknetty.listener;

import com.magicdroidx.raknetty.handler.session.Session;
import com.magicdroidx.raknetty.protocol.game.GamePacket;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public interface SessionListener {
    void registered(Session session);

    void connected(Session session);

    void packetReceived(Session session, GamePacket packet);

    void disconnected(Session session);
}
