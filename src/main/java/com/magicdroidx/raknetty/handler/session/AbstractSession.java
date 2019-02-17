package com.magicdroidx.raknetty.handler.session;

import com.magicdroidx.raknetty.RakNetServer;
import com.magicdroidx.raknetty.buffer.RakNetByteBuf;
import com.magicdroidx.raknetty.handler.session.future.FramePacketFuture;
import com.magicdroidx.raknetty.handler.session.future.FrameSetPacketFuture;
import com.magicdroidx.raknetty.handler.session.future.PacketFuture;
import com.magicdroidx.raknetty.listener.SessionListener;
import com.magicdroidx.raknetty.protocol.game.GamePacket;
import com.magicdroidx.raknetty.protocol.raknet.RakNetPacket;
import com.magicdroidx.raknetty.protocol.raknet.Reliability;
import com.magicdroidx.raknetty.protocol.raknet.session.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.ScheduledFuture;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public abstract class AbstractSession implements Session {

    private static final int ORDER_CHANNEL_COUNTS = 32;

    //Session Info
    int MTU;
    InetSocketAddress address;
    long GUID;

    SessionListener listener;
    ServerSessionHandler sessionHandler;
    ChannelHandlerContext ctx;
    private ScheduledFuture tickTask;

    private boolean idle = true;
    private long lastUpdateTime = System.currentTimeMillis();

    private PriorityBlockingQueue<PacketFuture> resendQueue = new PriorityBlockingQueue<>();
    private FragmentAggregator aggregator = new FragmentAggregator(this);

    //Inbound indexes
    private int inboundIndexSequenced = -1;
    private IndexWindow frameSetWindow = new IndexWindow();
    private IndexWindow reliableWindow = new IndexWindow();
    private HashMap<Integer, OrderChannel> orderChannels = new HashMap<>();

    //Outbound indexes
    private int outboundIndexFrameSet;
    private int outboundIndexReliable;
    private int outboundIndexOrdered;
    private int outboundIndexSequenced;
    private int outboundFragmentID;
    private Queue<GamePacket> sendQueue = new ConcurrentLinkedQueue<>();

    //Latency
    private long lastPingTime = System.currentTimeMillis();
    private int latency = 200;

    AbstractSession(ServerSessionHandler sessionHandler, InetSocketAddress address, ChannelHandlerContext ctx) {
        this.sessionHandler = sessionHandler;
        this.address = address;
        this.ctx = ctx;
        tickTask = sessionHandler.tickGroup().scheduleAtFixedRate(this::update0, 10, 10, TimeUnit.MILLISECONDS);

        for (int i = 0; i < ORDER_CHANNEL_COUNTS; i++) {
            orderChannels.put(i, new OrderChannel(this));
        }
    }

    private void update0() {
        //System.out.println("Ticking on thread: " + Thread.currentThread());
        if (state() == SessionState.CLOSED) {
            tickTask.cancel(true);
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (isIdle() && currentTime - lastUpdateTime >= this.getTimeOut()) {
            this.close("Timeout");
        }

        if (currentTime - lastPingTime >= 2000) {
            //Calculate latency every 2 seconds
            lastPingTime = currentTime;
            ConnectedPingPacket ping = new ConnectedPingPacket();
            ping.timestamp = currentTime;
            sendPacket(ping, Reliability.UNRELIABLE, true);
            return;
        }

        idle = true;

        //Dealing with Game Packet Queue
        if (!sendQueue.isEmpty()) {
            GamePacket p;
            GameWrapperPacket wrapperPacket = new GameWrapperPacket();
            while ((p = sendQueue.poll()) != null) {
                wrapperPacket.packets.add(p);
            }
            sendPacket(wrapperPacket, Reliability.RELIABLE_ORDERED_ACK);
        }

        //Dealing with Resend Queue
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

                //Bypass the RakNetPacketEncoder to avoid double encoding
                ctx.writeAndFlush(setFuture.envelop(address));

                setFuture.setSendTime(currentTime + getLatency());
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
                    //Send the old frame set first
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
            //Add it to resend queue
            FrameSetPacketFuture future = new FrameSetPacketFuture(frameSet, currentTime + getLatency());
            resendQueue.add(future);
        }

        AcknowledgePacket ACK = AcknowledgePacket.newACK();
        ACK.records.addAll(frameSetWindow.getOpened());
        if (!ACK.records.isEmpty()) {
            sendPacket(ACK, Reliability.UNRELIABLE, true);
        }

        AcknowledgePacket NACK = AcknowledgePacket.newNACK();
        NACK.records.addAll(frameSetWindow.getClosed());
        if (!NACK.records.isEmpty()) {
            sendPacket(NACK, Reliability.UNRELIABLE, true);
        }

        frameSetWindow.update();
        this.update();
    }

    public int getLatency() {
        return this.latency;
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
        return sessionHandler.server();
    }

    @Override
    public void close() {
        this.close("Unknown");
    }

    @Override
    public void close(String reason) {
        sendPacket(new DisconnectionNotificationPacket(), Reliability.UNRELIABLE, true);
        sessionHandler.close0(this, reason);
        tickTask.cancel(true);

        if (this.listener != null) {
            this.listener.disconnected(this);
        }
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
                            iterator.remove();
                        }

                        if (acknowledgePacket.isNACK()) {
                            future.setSendTime(System.currentTimeMillis());
                        }
                    }
                }
            }

            return;
        }

        if (packet instanceof ConnectedPingPacket) {
            ConnectedPongPacket response = new ConnectedPongPacket();
            response.timestamp = ((ConnectedPingPacket) packet).timestamp;
            sendPacket(response, Reliability.UNRELIABLE, true);
            return;
        }

        if (packet instanceof ConnectedPongPacket) {
            if (((ConnectedPongPacket) packet).timestamp == lastPingTime) {
                latency = (int) (System.currentTimeMillis() - lastPingTime);
            }
            return;
        }

        if (packet instanceof DisconnectionNotificationPacket) {
            this.close("Client Disconnected");
            return;
        }

        if (packet instanceof FrameSetPacket) {
            FrameSetPacket frameSet = (FrameSetPacket) packet;

            if (!frameSetWindow.openWindow(frameSet.index)) {
                //Duplicate frame set
                return;
            }

            for (FramePacket frame : frameSet.frames()) {
                if (frame.reliability.isReliable()) {
                    if (!reliableWindow.openWindow(frame.indexReliable)) {
                        //Duplicate frame
                        continue;
                    }
                }

                if (frame.fragmented) {
                    FramePacket result = aggregator.offer(frame);
                    if (result != null) {
                        frame = result;
                    } else {
                        continue;
                    }
                }

                if (frame.reliability.isSequenced()) {
                    if (frame.indexSequenced > inboundIndexSequenced) {
                        inboundIndexSequenced = frame.indexSequenced;
                    } else {
                        continue;
                    }
                } else if (frame.reliability.isOrdered()) {
                    OrderChannel channel = orderChannels.get(frame.orderChannel);
                    if (channel == null) {
                        continue;
                    }

                    channel.provide(frame);
                    continue;
                }

                handle(SessionPacket.from(frame.body));
            }

            return;
        }

        if (packet instanceof GameWrapperPacket) {

            if (this.listener != null) {
                for (GamePacket p : ((GameWrapperPacket) packet).packets) {
                    this.listener.packetReceived(this, p);
                }
            }

            this.lastUpdateTime = System.currentTimeMillis();
            this.idle = false;

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

        RakNetByteBuf buf = RakNetByteBuf.buffer();
        packet.write(buf);

        if (!reliability.isReliable()) {
            FramePacket frame = new FramePacket();
            frame.reliability = reliability;
            frame.body = buf;
            sendPacket(frame, immediate);
            return;
        }

        //If the packet cannot be put into single frame set packet, split it.
        int maxSize = MTU
                - FrameSetPacket.OVERHEAD_LENGTH
                - FramePacket.OVERHEAD_LENGTH
                - reliability.length();

        if (buf.writerIndex() > maxSize) {

            //Cut packet into pieces; reduce the fragment overhead in advance.
            int chunkSize = maxSize - FramePacket.FRAGMENT_OVERHEAD_LENGTH;

            int fragmentID = outboundFragmentID++;
            int fragmentIndex = 0;
            int fragmentCount = (int) Math.ceil(1.0d * buf.readableBytes() / chunkSize);

            // All fragments share to same ordered or sequenced index
            int indexSequenced = outboundIndexSequenced;
            int indexOrdered = outboundIndexOrdered;
            if (reliability.isSequenced()) {
                indexSequenced = outboundIndexSequenced++;
            } else if (reliability.isOrdered()) {
                indexOrdered = outboundIndexOrdered++;
            }

            while (buf.isReadable()) {
                ByteBuf fragment = buf.readBytes(Math.min(chunkSize, buf.readableBytes()));
                FramePacket frame = new FramePacket();
                frame.fragmented = true;
                frame.fragmentID = fragmentID;
                frame.fragmentIndex = fragmentIndex++;
                frame.fragmentCount = fragmentCount;
                frame.fragment = fragment;
                frame.reliability = reliability;
                frame.indexReliable = outboundIndexReliable++;
                frame.indexSequenced = indexSequenced;
                frame.indexOrdered = indexOrdered;

                //Add them to the resend queue or send them immediately.
                sendPacket(frame, immediate);
            }

        } else {
            FramePacket frame = new FramePacket();
            frame.body = buf;
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
    public void sendPacket(GamePacket packet) {
        sendQueue.add(packet);
    }

    @Override
    public int getTimeOut() {
        return state() == SessionState.CONNECTED ? 10000 : 3000;
    }

    protected abstract boolean packetReceived(SessionPacket packet);

    @Override
    public void setListener(SessionListener listener) {
        this.listener = listener;
    }

    @Override
    public SessionListener listener() {
        return this.listener;
    }

    @Override
    public String toString() {
        return "AbstractSession{" +
                "MTU=" + MTU +
                ", address=" + address +
                ", GUID=" + GUID +
                ", listener=" + listener +
                ", sessionHandler=" + sessionHandler +
                ", ctx=" + ctx +
                ", tickTask=" + tickTask +
                ", idle=" + idle +
                ", lastUpdateTime=" + lastUpdateTime +
                ", resendQueue=" + resendQueue +
                ", aggregator=" + aggregator +
                ", inboundIndexSequenced=" + inboundIndexSequenced +
                ", frameSetWindow=" + frameSetWindow +
                ", reliableWindow=" + reliableWindow +
                ", orderChannels=" + orderChannels +
                ", outboundIndexFrameSet=" + outboundIndexFrameSet +
                ", outboundIndexReliable=" + outboundIndexReliable +
                ", outboundIndexOrdered=" + outboundIndexOrdered +
                ", outboundIndexSequenced=" + outboundIndexSequenced +
                ", outboundFragmentID=" + outboundFragmentID +
                ", sendQueue=" + sendQueue +
                ", lastPingTime=" + lastPingTime +
                ", latency=" + latency +
                '}';
    }
}
