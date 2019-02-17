package com.magicdroidx.raknetty.handler.session;

import com.google.common.base.Preconditions;
import com.magicdroidx.raknetty.protocol.raknet.session.FramePacket;
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


    /**
     * @param frame
     * @return return a FramePacket if all fragments have arrived, otherwise return null.
     */
    public FramePacket offer(FramePacket frame) {
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
                return null;
            }
        }

        fragmentPool.remove(fragmentID);

        //Put all the fragments back into the last frame packet provided in order to go through the order channel.
        ByteBuf fullBuf = Unpooled.copiedBuffer(fragments);
        frame.fragmented = false;
        frame.body = fullBuf;
        //session.handle(SessionPacket.from(fullBuf));
        return frame;
    }

}
