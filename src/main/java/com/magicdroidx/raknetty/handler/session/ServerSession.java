package com.magicdroidx.raknetty.handler.session;

import com.google.common.base.Preconditions;
import com.magicdroidx.raknetty.RakNetty;
import com.magicdroidx.raknetty.protocol.raknet.Reliability;
import com.magicdroidx.raknetty.protocol.raknet.session.*;
import com.magicdroidx.raknetty.protocol.raknet.unconnected.IncompatibleProtocolPacket;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

/**
 * RakNetty Project
 * Author: MagicDroidX
 */
public class ServerSession extends AbstractSession {

    private SessionState state = SessionState.UNCONNECTED;

    public ServerSession(SessionManager sessionManager, InetSocketAddress address, ChannelHandlerContext ctx) {
        super(sessionManager, address, ctx);
    }

    @Override
    public SessionState state() {
        return state;
    }

    public void update() {

    }

    @Override
    protected boolean packetReceived(SessionPacket conn) {

        //Handle Connection Request 1
        if (conn instanceof OpenConnectionRequestPacket1 && this.state() == SessionState.UNCONNECTED) {
            OpenConnectionRequestPacket1 request = (OpenConnectionRequestPacket1) conn;
            System.out.println("Connection Request 1 (protocolVersion=" + request.protocolVersion + ", MTU=" + request.MTU + ")");

            //If the protocol is incompatible
            if (request.protocolVersion != RakNetty.PROTOCOL_VERSION) {
                IncompatibleProtocolPacket response = new IncompatibleProtocolPacket();
                response.protocolVersion = RakNetty.PROTOCOL_VERSION;
                response.serverGUID = server().uuid().getMostSignificantBits();
                sendPacket(response, Reliability.UNRELIABLE);
                this.close("Incompatible Protocol: " + request.protocolVersion);
                return false;
            }

            //Check MTU
            Preconditions.checkState(request.MTU <= sessionManager.server().getMTU(), "Client requested a MTU which exceeds the maximum.");
            setMTU(request.MTU);

            //Response to the client
            OpenConnectionResponsePacket1 response = new OpenConnectionResponsePacket1();
            response.MTU = request.MTU;
            response.serverGUID = server().uuid().getMostSignificantBits();
            sendPacket(response, Reliability.UNRELIABLE);

            //Set the state to CONNECTION_OPENING
            this.state = SessionState.CONNECTION_OPENING;
            return true;
        }

        //Handle Connection Request 2
        if (conn instanceof OpenConnectionRequestPacket2 && this.state() == SessionState.CONNECTION_OPENING) {
            OpenConnectionRequestPacket2 request = (OpenConnectionRequestPacket2) conn;
            System.out.println("Connection Request 2 (serverAddress=" + request.serverAddress + ", MTU=" + request.MTU + ", ClientGUID=" + request.clientGUID + ")");

            //CheckMTU
            Preconditions.checkState(request.MTU <= getMTU(), "Client requested a MTU which exceeds the maximum.");
            setMTU(request.MTU);

            //Response to the client
            OpenConnectionResponsePacket2 response = new OpenConnectionResponsePacket2();
            response.serverGUID = server().uuid().getMostSignificantBits();
            response.clientAddress = address();
            response.MTU = getMTU();
            sendPacket(response, Reliability.UNRELIABLE);

            this.state = SessionState.CONNECTION_REQUESTING;
            this.GUID = request.clientGUID;
            return true;
        }

        if (conn instanceof ConnectionRequestPacket && this.state() == SessionState.CONNECTION_REQUESTING) {
            ConnectionRequestPacket request = (ConnectionRequestPacket) conn;
            System.out.println("Client Connect(clientGUID=" + request.clientGUID + ", timestamp=" + request.timestamp + ", security=" + request.hasSecurity + ")");

            //Check ClientGUID
            Preconditions.checkState(GUID == request.clientGUID, "Client GUID does not match");

            //Response to the client
            ConnectionRequestAcceptedPacket response = new ConnectionRequestAcceptedPacket();
            response.clientAddress = address;
            response.incomingTimestamp = request.timestamp;
            response.serverTimestamp = System.currentTimeMillis();
            sendPacket(response, Reliability.UNRELIABLE);

            this.state = SessionState.CONNECTION_REQUEST_ACCEPTED;
            return true;
        }

        ctx.fireChannelRead(conn.envelop(address));
        return false;
    }

}
