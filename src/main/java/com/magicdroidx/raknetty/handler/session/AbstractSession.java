package com.magicdroidx.raknetty.handler.session;

import com.magicdroidx.raknetty.RakNetServer;
import com.magicdroidx.raknetty.handler.session.future.FramePacketFuture;
import com.magicdroidx.raknetty.handler.session.future.FrameSetPacketFuture;
import com.magicdroidx.raknetty.handler.session.future.PacketFuture;
import com.magicdroidx.raknetty.protocol.raknet.RakNetPacket;
import com.magicdroidx.raknetty.protocol.raknet.Reliability;
import com.magicdroidx.raknetty.protocol.raknet.session.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.ScheduledFuture;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public abstract class AbstractSession implements Session {

    //Session Info
    int MTU;
    InetSocketAddress address;
    long GUID;

    SessionManager sessionManager;
    ChannelHandlerContext ctx;
    private ScheduledFuture tickTask;

    private boolean idle = true;
    private long lastUpdateTime = System.currentTimeMillis();

    private PriorityBlockingQueue<PacketFuture> resendQueue = new PriorityBlockingQueue<>();
    private FragmentAggregator aggregator = new FragmentAggregator(this);

    //Inbound indexes
    private int inboundIndexFrameSet;
    private int inboundIndexReliable;
    private int inboundIndexOrdered;
    private int inboundIndexSequenced;
    private int inboundFragmentID;

    //Outbound indexes
    private int outboundIndexFrameSet;
    private int outboundIndexReliable;
    private int outboundIndexOrdered;
    private int outboundIndexSequenced;
    private int outboundFragmentID;

    private AcknowledgePacket ACK = AcknowledgePacket.newACK();
    private AcknowledgePacket NACK = AcknowledgePacket.newNACK();

    AbstractSession(SessionManager sessionManager, InetSocketAddress address, ChannelHandlerContext ctx) {
        this.sessionManager = sessionManager;
        this.address = address;
        this.ctx = ctx;
        tickTask = ctx.executor().scheduleAtFixedRate(this::update0, 10, 10, TimeUnit.MILLISECONDS);
    }

    private void update0() {
        long currentTime = System.currentTimeMillis();
        if (isIdle() && currentTime - lastUpdateTime >= this.getTimeOut()) {
            this.close("Timeout");
        }

        idle = true;

        PacketFuture packetFuture;

        FrameSetPacket frameSet = null;
        while ((packetFuture = resendQueue.poll()) != null) {
            if (packetFuture.sendTime() >= currentTime) {
                //No packet to send
                resendQueue.add(packetFuture);
                break;
            }

            if (packetFuture instanceof FrameSetPacketFuture) {
                FrameSetPacketFuture setFuture = (FrameSetPacketFuture) packetFuture;
                ctx.writeAndFlush(setFuture.packet().envelop(address));
                setFuture.sendTime(currentTime + getLatency());
                continue;
            }

            if (packetFuture instanceof FramePacketFuture) {
                FramePacketFuture frameFuture = (FramePacketFuture) packetFuture;

                if (frameSet == null) {
                    frameSet = new FrameSetPacket();
                    frameSet.index = outboundIndexFrameSet++;
                }

                FramePacket packet = frameFuture.packet();
                if (frameSet.length() + packet.length() > getMTU()) {
                    //Send the frame set first
                    FrameSetPacketFuture setFuture = new FrameSetPacketFuture(frameSet, currentTime);
                    resendQueue.add(setFuture);

                    //Create new frame set
                    frameSet = new FrameSetPacket();
                    frameSet.index = outboundIndexFrameSet++;
                }

                frameSet.frames().add(packet);
            }
        }

        if (frameSet != null) {
            ctx.writeAndFlush(frameSet.envelop(address)); //Send it immediately
            FrameSetPacketFuture future = new FrameSetPacketFuture(frameSet, currentTime + getLatency());
            resendQueue.add(future);
            System.out.println("Added a frame set to queue\n" + resendQueue);
        }

        //TODO: ACK and NACK

        this.update();
    }

    public int getLatency() {
        //TODO
        return 1000;
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
        sendPacket(new DisconnectionNotificationPacket(), Reliability.UNRELIABLE);
    }

    @Override
    public final void handle(SessionPacket packet) {
        if (packet instanceof AcknowledgePacket) {
            AcknowledgePacket acknowledgePacket = (AcknowledgePacket) packet;

            Iterator<PacketFuture> iterator = resendQueue.iterator();
            while (iterator.hasNext()) {
                PacketFuture future = iterator.next();
                if (future instanceof FrameSetPacketFuture) {
                    int index = ((FrameSetPacketFuture) future).frameSetIndex();

                    if (acknowledgePacket.records().contains(index)) {
                        if (acknowledgePacket.isACK()) {
                            System.out.println("ACKed " + index);
                            iterator.remove();
                        }

                        if (acknowledgePacket.isNACK()) {
                            System.out.println("NACKed " + index);
                            future.sendTime(System.currentTimeMillis());
                        }
                    }
                }
            }

            return;
        }

        if (packet instanceof FrameSetPacket) {
            FrameSetPacket frameSet = (FrameSetPacket) packet;
            for (FramePacket frame : frameSet.frames()) {
                if (frame.fragmented) {
                    aggregator.offer(frame);
                } else {
                    //TODO: Ordered and sequenced
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

    protected void sendPacket(FramePacket packet) {
        sendPacket(packet, false);
    }

    @Override
    public void sendPacket(RakNetPacket packet, Reliability reliability) {
        sendPacket(packet, reliability, false);
    }

    protected void sendPacket(FramePacket packet, boolean immediate) {
        if (immediate) {
            FrameSetPacket frameSet = new FrameSetPacket();
            frameSet.index = outboundIndexFrameSet++;
            frameSet.frames().add(packet);
            ctx.writeAndFlush(frameSet.envelop(address));
        } else {
            resendQueue.add(new FramePacketFuture(packet, System.currentTimeMillis(), packet.reliability));
        }
    }

    public void sendPacket(RakNetPacket packet, Reliability reliability, boolean immediate) {
        long present = System.currentTimeMillis();

        if (!(packet instanceof SessionPacket) || packet instanceof FramelessPacket) {
            ctx.writeAndFlush(packet.envelop(address));
            return;
        }

        if (packet instanceof FrameSetPacket) {
            if (immediate) {
                ctx.writeAndFlush(packet.envelop(address));
            } else {
                resendQueue.add(new FrameSetPacketFuture((FrameSetPacket) packet, present));
            }
            return;
        }

        packet.encode();

        if (!reliability.isReliable()) {
            FramePacket frame = new FramePacket();
            frame.reliability = reliability;
            frame.body = (SessionPacket) packet;
            sendPacket(frame, immediate);
            return;
        }

        //If the packet cannot be put into single frame set packet, split it.
        int maxSize = MTU
                - FrameSetPacket.OVERHEAD_LENGTH
                - FramePacket.OVERHEAD_LENGTH
                - reliability.length();
        if (packet.writerIndex() > maxSize) {
            int chunkSize = maxSize - FramePacket.FRAGMENT_OVERHEAD_LENGTH;
            int fragmentID = outboundFragmentID++;
            while (packet.isReadable()) {
                ByteBuf buf = packet.readBytes(Math.min(chunkSize, packet.readableBytes()));
                FramePacket frame = new FramePacket();
                frame.fragmentID = fragmentID;
                frame.fragmented = true;
                frame.fragment = buf;
                frame.reliability = reliability;
                frame.indexReliable = outboundIndexReliable++;
                if (reliability.isSequenced()) {
                    frame.indexSequenced = outboundIndexSequenced++;
                } else if (reliability.isOrdered()) {
                    frame.indexOrdered = outboundIndexOrdered++;
                }
                sendPacket(frame, immediate);
            }
        } else {
            FramePacket frame = new FramePacket();
            frame.body = (SessionPacket) packet;
            frame.reliability = reliability;
            frame.indexReliable = outboundIndexReliable++;
            if (reliability.isSequenced()) {
                frame.indexSequenced = outboundIndexSequenced++;
            } else if (reliability.isOrdered()) {
                frame.indexOrdered = outboundIndexOrdered++;
            }
            sendPacket(frame, immediate);
        }
    }

    @Override
    public int getTimeOut() {
        return state() == SessionState.CONNECTED ? 10000 : 3000;
    }

    protected abstract boolean packetReceived(SessionPacket packet);
}
