package com.magicdroidx.raknetty.handler;

import com.magicdroidx.raknetty.RakNetServer;
import com.magicdroidx.raknetty.protocol.raknet.AddressedRakNetPacket;
import com.magicdroidx.raknetty.protocol.raknet.unconnected.UnconnectedPingPacket;
import com.magicdroidx.raknetty.protocol.raknet.unconnected.UnconnectedPongPacket;
import io.netty.channel.ChannelHandlerContext;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class UnconnectedPingHandler extends RakNetPacketHandler<UnconnectedPingPacket> {

    private RakNetServer server;

    public UnconnectedPingHandler(RakNetServer server) {
        super(UnconnectedPingPacket.class);
        this.server = server;
    }

    @Override
    protected void packetReceived(ChannelHandlerContext ctx, AddressedRakNetPacket<UnconnectedPingPacket> p) {
        UnconnectedPingPacket ping = p.content();

        UnconnectedPongPacket pong = new UnconnectedPongPacket();
        pong.pingId = ping.pingId;
        pong.serverGUID = server.uuid().getMostSignificantBits();
        //TODO: Customize
        pong.serverName = "MCPE;Hello! RakNetty!;113;1.1.4;0;20;8964;Realm;Creative";

        ctx.writeAndFlush(new AddressedRakNetPacket<>(pong, p.sender()));
    }
}
