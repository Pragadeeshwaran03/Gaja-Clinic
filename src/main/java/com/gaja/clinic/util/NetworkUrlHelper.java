package com.gaja.clinic.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public final class NetworkUrlHelper {

    private NetworkUrlHelper() {
    }

    public static String getServerLanIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (!ni.isUp() || ni.isLoopback()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        String ip = addr.getHostAddress();
                        if (isPrivateLanIp(ip)) {
                            return ip;
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            // fall through
        }
        return null;
    }

    public static String buildSuggestedLocalUrl(int port, boolean useSslip) {
        String lanIp = getServerLanIp();
        if (lanIp == null || lanIp.isBlank()) {
            return "http://localhost:" + port;
        }
        String host = useSslip ? toSslipHost(lanIp) : lanIp;
        String portSuffix = (port == 80 || port == 443) ? "" : ":" + port;
        return "http://" + host + portSuffix;
    }

    public static String toSslipHost(String ip) {
        return ip.replace('.', '-') + ".sslip.io";
    }

    public static boolean isPrivateLanIp(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        try {
            int b0 = Integer.parseInt(parts[0]);
            int b1 = Integer.parseInt(parts[1]);
            return b0 == 10
                    || (b0 == 172 && b1 >= 16 && b1 <= 31)
                    || (b0 == 192 && b1 == 168);
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
