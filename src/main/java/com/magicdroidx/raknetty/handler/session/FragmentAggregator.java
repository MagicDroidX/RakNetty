package com.magicdroidx.raknetty.handler.session;

import com.google.common.base.Preconditions;
import com.magicdroidx.raknetty.protocol.raknet.session.FramePacket;
import com.magicdroidx.raknetty.protocol.raknet.session.SessionPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.HashMap;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class FragmentAggregator {

    private Session session;

    private HashMap<Integer, ByteBuf[]> fragmentPool = new HashMap<>();

    public FragmentAggregator(Session session) {
        this.session = session;
    }

    public void offer(FramePacket frame) {
        Preconditions.checkState(frame.fragmented, "Only accept fragmented frame packet");

        int fragmentIndex = frame.fragmentIndex;
        int fragmentID = frame.fragmentID;
        int fragmentCount = frame.fragmentCount;
        ByteBuf[] fragments;
        //TODO: Add pool size checking
        if (!fragmentPool.containsKey(fragmentID)) {
            fragmentPool.put(fragmentID, fragments = new ByteBuf[fragmentCount]);
        } else {
            fragments = fragmentPool.get(fragmentID);
        }

        fragments[fragmentIndex] = frame.fragment;

        //Check if all fragments arrived
        for (ByteBuf buf : fragments) {
            if (buf == null) {
                return;
            }
        }

        fragmentPool.remove(fragmentID);

        ByteBuf fullBuf = Unpooled.copiedBuffer(fragments);

        session.handle(SessionPacket.from(fullBuf));
    }

}
