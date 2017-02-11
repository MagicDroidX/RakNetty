package com.magicdroidx.raknetty.handler.session;

import com.magicdroidx.raknetty.RakNetServer;
import com.magicdroidx.raknetty.handler.RakNetPacketHandler;
import com.magicdroidx.raknetty.protocol.raknet.AddressedRakNetPacket;
import com.magicdroidx.raknetty.protocol.raknet.Reliability;
import com.magicdroidx.raknetty.protocol.raknet.session.*;
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

    public Session get(InetSocketAddress address, boolean create) {
        Session session = sessions.get(address);
        if (session == null && create) {
            session = new ServerSession(this, address, ctx);
            this.sessions.put(address, (ServerSession) session);
            System.out.println("Create new session for " + session.address());
        }

        return session;
    }

    void close(Session session, String reason) {
        if (session == null) {
            return;
        }

        //TODO: Send Disconnect Packet
        FrameSetPacket frameSet = new FrameSetPacket();
        FramePacket frame = new FramePacket();
        frame.reliability = Reliability.RELIABLE_ORDERED;
        frame.body = new DisconnectPacket();
        frameSet.frames().add(frame);
        //frame.index = 0x0;
        ctx.writeAndFlush(frameSet.envelop(session.address()));
        sessions.remove(session.address());
        System.out.println("Closed a session due to " + reason);
    }

    @Override
    protected void packetReceived(ChannelHandlerContext ctx, AddressedRakNetPacket<SessionPacket> p) {
        this.ctx = ctx;
        SessionPacket conn = p.content();
        InetSocketAddress sender = p.sender();
        Session session = get(sender, conn instanceof ConnectionRequestPacket1);

        if (session instanceof ServerSession) {
            session.handle(conn);
            return;
        }

        ctx.fireChannelRead(p.retain());
    }
}
