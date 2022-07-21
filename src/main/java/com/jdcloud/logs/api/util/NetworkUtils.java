package com.jdcloud.logs.api.util;

import com.jdcloud.logs.api.common.Constants;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public final class NetworkUtils {

    private NetworkUtils() {
    }

    public static boolean isIpAddress(final String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return false;
        }
        try {
            final String[] tokens = ipAddress.split("\\.");
            if (tokens.length != 4) {
                return false;
            }
            for (String token : tokens) {
                int i = Integer.parseInt(token);
                if (i < 0 || i > 255) {
                    return false;
                }
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static String getLocalMachineIp() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                if (!ni.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    final InetAddress address = addresses.nextElement();
                    if (!address.isLinkLocalAddress() && address.getHostAddress() != null) {
                        String ipAddress = address.getHostAddress();
                        if (ipAddress.equals(Constants.CONST_LOCAL_IP)) {
                            continue;
                        }
                        if (isIpAddress(ipAddress)) {
                            return ipAddress;
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            // swallow it
        }
        return null;
    }
}
