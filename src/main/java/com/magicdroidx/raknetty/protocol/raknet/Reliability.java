package com.magicdroidx.raknetty.protocol.raknet;

import java.util.HashMap;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public enum Reliability {

    UNRELIABLE
            (0, false, false, false, false),
    UNRELIABLE_SEQUENCED
            (1, false, true, true, false),
    RELIABLE
            (2, true, false, false, false),
    RELIABLE_ORDERED
            (3, true, true, false, false),
    RELIABLE_SEQUENCED
            (4, true, true, true, false),
    UNRELIABLE_ACK
            (5, false, false, false, true),
    RELIABLE_ACK
            (6, true, false, false, true),
    RELIABLE_ORDERED_ACK
            (7, true, true, false, true);

    int id;
    boolean reliable;
    boolean ordered;
    boolean sequenced;
    boolean withAckReceipt;

    Reliability(int id, boolean reliable, boolean ordered, boolean sequenced, boolean withAckReceipt) {
        this.id = id;
        this.reliable = reliable;
        this.ordered = ordered;
        this.sequenced = sequenced;
        this.withAckReceipt = withAckReceipt;
    }

    public int length() {
        int length = 0;
        if (isReliable()) {
            length += 3;
        }

        if (isSequenced()) {
            length += 3;
        }

        if (isOrdered()) {
            length += 3;
            length += 1;
        }

        return length;
    }

    public int id() {
        return id;
    }

    public boolean isReliable() {
        return reliable;
    }

    public boolean isOrdered() {
        return ordered;
    }

    public boolean isSequenced() {
        return sequenced;
    }

    public boolean withAckReceipt() {
        return withAckReceipt;
    }

    private final static HashMap<Integer, Reliability> lookUpMap = new HashMap<>();

    static {
        for (Reliability reliability : values()) {
            lookUpMap.put(reliability.id(), reliability);
        }
    }

    public static Reliability getById(int id) {
        id &= 0b111;
        return lookUpMap.get(id);
    }
}
