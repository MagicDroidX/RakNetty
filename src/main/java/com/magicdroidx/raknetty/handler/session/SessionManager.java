package com.magicdroidx.raknetty.handler.session;

import com.magicdroidx.raknetty.RakNetServer;
import com.magicdroidx.raknetty.handler.RakNetPacketHandler;
import com.magicdroidx.raknetty.protocol.raknet.AddressedRakNetPacket;
import com.magicdroidx.raknetty.protocol.raknet.session.OpenConnectionRequestPacket1;
import com.magicdroidx.raknetty.protocol.raknet.session.SessionPacket;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class SessionManager extends RakNetPacketHandler<SessionPacket> {

    private RakNetServer server;

    private Map<InetSocketAddress, ServerSession> sessions = new ConcurrentHashMap<>();

    private ChannelHandlerContext ctx;

    public SessionManager(RakNetServer server) {
        super(SessionPacket.class);
        this.server = server;
    }

    public RakNetServer server() {
        return server;
    }

    public boolean contains(InetSocketAddress address) {
        return sessions.containsKey(address);
    }

    public Session get(InetSocketAddress address) {
        return get(address, false);
    }

    Session get(InetSocketAddress address, boolean create) {
        Session session = sessions.get(address);
        if (session == null && create) {

            session = new ServerSession(this, address, ctx);
            this.sessions.put(address, (ServerSession) session);

            if (server().listener() != null) {
                server().listener().onSessionCreated(session);
            }
        }

        return session;
    }

    public void close(InetSocketAddress address, String reason) {
        Session session = this.get(address);
        if (session != null) {
            session.close(reason);
        }
    }

    void close(Session session, String reason) {
        if (session == null) {
            return;
        }

        sessions.remove(session.address());
        if (server().listener() != null) {
            server().listener().onSessionRemoved(session);
        }
    }

    @Override
    protected void packetReceived(ChannelHandlerContext ctx, AddressedRakNetPacket<SessionPacket> p) {
        this.ctx = ctx;
        SessionPacket conn = p.content();
        InetSocketAddress sender = p.sender();
        Session session = get(sender, conn instanceof OpenConnectionRequestPacket1);

        if (session instanceof ServerSession) {
            session.handle(conn);
            return;
        }

        ctx.fireChannelRead(p.retain());
    }
}
