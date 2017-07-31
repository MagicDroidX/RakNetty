package com.magicdroidx.raknetty.listener;

import com.magicdroidx.raknetty.handler.session.Session;
import com.magicdroidx.raknetty.protocol.game.GamePacket;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class SessionListenerAdapter implements SessionListener {
    @Override
    public void registered(Session session) {

    }

    @Override
    public void connected(Session session) {

    }

    @Override
    public void packetReceived(Session session, GamePacket packet) {

    }

    @Override
    public void disconnected(Session session) {

    }
}
