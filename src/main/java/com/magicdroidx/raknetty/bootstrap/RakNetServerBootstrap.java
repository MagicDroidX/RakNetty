package com.magicdroidx.raknetty.bootstrap;

import com.magicdroidx.raknetty.listener.ServerListener;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public interface RakNetServerBootstrap extends RakNetBootstrap {

    RakNetServerBootstrap withListener(ServerListener listener);
}
