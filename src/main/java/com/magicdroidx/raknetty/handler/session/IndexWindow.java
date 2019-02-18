package com.magicdroidx.raknetty.handler.session;

import java.util.HashSet;
import java.util.Set;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class IndexWindow {

    private static final int MAX_SIZE = 128;
    private int start = -1;
    private int end = -1;
    private Set<Integer> opened = new HashSet<>();

    public void flush() {
        start = end;
    }

    public boolean openWindow(int index) {
        if (index > start && index - start < MAX_SIZE) {
            if (opened.contains(index)) {
                return false;
            }

            opened.add(index);
            end = Math.max(end, index);
            return true;
        }

        return false;
    }

    public void update() {
        while (!opened.isEmpty()) {
            if (opened.contains(start + 1)) { //Find next closable window
                opened.remove(++start);
            } else {
                break;
            }
        }
    }

    public Set<Integer> getOpened() {
        return opened;
    }

    public Set<Integer> getClosed() {
        Set<Integer> closed = new HashSet<>();
        for (int i = start + 1; i <= end; i++) {
            if (!opened.contains(i)) {
                closed.add(i);
            }
        }

        return closed;
    }

    public int start() {
        return start;
    }

    public int end() {
        return end;
    }
}
