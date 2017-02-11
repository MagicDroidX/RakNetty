package com.magicdroidx.raknetty.handler.session;

import com.magicdroidx.raknetty.RakNetServer;
import com.magicdroidx.raknetty.protocol.raknet.RakNetPacket;
import com.magicdroidx.raknetty.protocol.raknet.Reliability;
import com.magicdroidx.raknetty.protocol.raknet.session.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.ScheduledFuture;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public abstract class AbstractSession implements Session {

    int MTU;
    InetSocketAddress address;
    long GUID;

    SessionManager sessionManager;
    ChannelHandlerContext ctx;

    private boolean idle = true;
    private long lastUpdateTime = System.currentTimeMillis();
    private ScheduledFuture tickTask;

    int outboundFrameIndex;

    AbstractSession(SessionManager sessionManager, InetSocketAddress address, ChannelHandlerContext ctx) {
        this.sessionManager = sessionManager;
        this.address = address;
        this.ctx = ctx;
        tickTask = ctx.executor().scheduleAtFixedRate(this::update0, 20, 50, TimeUnit.MILLISECONDS);
    }

    private void update0() {
        long currentTime = System.currentTimeMillis();
        if (isIdle() && currentTime - lastUpdateTime >= this.getTimeOut()) {
            this.close("Timeout");
        }

        idle = true;

        this.update();
    }

    @Override
    public int getMTU() {
        return MTU;
    }

    @Override
    public void setMTU(int MTU) {
        this.MTU = MTU;
    }

    public boolean isIdle() {
        return idle;
    }

    @Override
    public InetSocketAddress address() {
        return address;
    }

    protected RakNetServer server() {
        return sessionManager.server();
    }

    @Override
    public void close() {
        this.close("Unknown");
    }

    @Override
    public void close(String reason) {
        sessionManager.close(this, reason);
        tickTask.cancel(true);
        sendPacket(new DisconnectPacket());
    }

    @Override
    public final void handle(SessionPacket packet) {
        if (packet instanceof FrameSetPacket) {
            //TODO: a lot work here
            FrameSetPacket frameSet = (FrameSetPacket) packet;
            for (FramePacket frame : frameSet.frames()) {
                if (!frame.fragmented) {
                    handle(frame.body);
                }
            }

            return;
        }

        if (this.packetReceived(packet)) {
            this.lastUpdateTime = System.currentTimeMillis();
            this.idle = false;
        }
    }

    protected void sendPacket(RakNetPacket packet) {

        if (!(packet instanceof SessionPacket) || packet instanceof FramelessPacket) {
            ctx.writeAndFlush(packet.envelop(address));
            return;
        }

        //TODO: Frame
        FrameSetPacket frameSet = new FrameSetPacket();
        frameSet.index = outboundFrameIndex++;
        FramePacket frame = new FramePacket();
        frame.body = (SessionPacket) packet;
        frame.reliability = Reliability.RELIABLE_ORDERED;
        frameSet.frames.add(frame);
        ctx.writeAndFlush(frameSet.envelop(address));
    }

    protected abstract boolean packetReceived(SessionPacket packet);
}
