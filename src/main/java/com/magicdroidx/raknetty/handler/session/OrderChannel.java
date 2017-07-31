package com.magicdroidx.raknetty.handler.session;

import com.magicdroidx.raknetty.protocol.raknet.session.FramePacket;
import com.magicdroidx.raknetty.protocol.raknet.session.SessionPacket;

import java.util.HashMap;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class OrderChannel {

    private static final int MAX_SIZE = 128;
    private int start = -1;
    private int end = -1;
    private HashMap<Integer, FramePacket> packets = new HashMap<>();

    private Session session;

    public OrderChannel(Session session) {
        this.session = session;
    }

    public void provide(FramePacket packet) {
        if (packet.reliability.isOrdered() && !packet.reliability.isSequenced()) {
            int index = packet.indexOrdered;

            if (index > start && index - start < MAX_SIZE) {
                if (packets.containsKey(index)) {
                    return;
                }

                packets.put(index, packet);
                end = Math.max(end, index);

                process();
            }

        }
    }

    private void process() {
        while (!packets.isEmpty()) {
            if (packets.containsKey(start + 1)) { //Find next processable packet
                start++;
                FramePacket packet = packets.get(start);
                packets.remove(start);
                session.handle(SessionPacket.from(packet.body));
            } else {
                break;
            }
        }
    }
}
