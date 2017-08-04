package com.magicdroidx.raknetty.listener;

import com.magicdroidx.raknetty.handler.session.Session;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public interface ServerListener {
    void onSessionCreated(Session session);

    void onSessionRemoved(Session session, String reason);
}
