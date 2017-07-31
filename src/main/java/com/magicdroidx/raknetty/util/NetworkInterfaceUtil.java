package com.magicdroidx.raknetty.util;

import java.net.*;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * raknetty Project
 * Author: MagicDroidX
 */
public class NetworkInterfaceUtil {

    public static int getMTU() throws UnknownHostException, SocketException {
        return NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getMTU();
    }

    public static InetSocketAddress[] getSystemAddresses(int port) throws SocketException {
        Set<InetSocketAddress> addresses = new HashSet<>();
        Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
        while (e.hasMoreElements()) {
            NetworkInterface i = e.nextElement();

            Enumeration<InetAddress> addressEnum = i.getInetAddresses();

            while (addressEnum.hasMoreElements()) {
                InetAddress address = addressEnum.nextElement();
                addresses.add(new InetSocketAddress(address, port));
            }
        }

        return addresses.toArray(new InetSocketAddress[0]);
    }
}
