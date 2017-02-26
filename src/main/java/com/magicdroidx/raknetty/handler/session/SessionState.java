package com.magicdroidx.raknetty.handler.session;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public enum SessionState {
    UNCONNECTED,
    CONNECTION_OPENING,
    CONNECTION_REQUESTING,
    CONNECTION_REQUEST_ACCEPTED,
    CONNECTED,
    CLOSED
}
