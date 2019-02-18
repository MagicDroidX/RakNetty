package com.magicdroidx.raknetty.handler.session;

import com.magicdroidx.raknetty.listener.SessionListener;
import com.magicdroidx.raknetty.protocol.game.GamePacket;
import com.magicdroidx.raknetty.protocol.raknet.RakNetPacket;
import com.magicdroidx.raknetty.protocol.raknet.Reliability;
import com.magicdroidx.raknetty.protocol.raknet.session.SessionPacket;

import java.net.InetSocketAddress;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public interface Session {

    SessionState state();

    InetSocketAddress address();

    void close();

    void close(String reason);

    int getTimeOut();

    int getMTU();

    void setMTU(int mtu);

    void update();

    void handle(SessionPacket packet);

    void sendPacket(GamePacket packet);

    void sendPacketDirect(GamePacket packet);

    void sendPacket(RakNetPacket packet, Reliability reliability);

    void sendPacket(RakNetPacket packet, Reliability reliability, boolean immediate);

    void setListener(SessionListener listener);

    SessionListener listener();
}
