package com.betmotion.agentsmanagement.utils;

import javax.servlet.http.HttpServletRequest;

public class IPUtils {

    public static String extractIpAddress(HttpServletRequest request) {
        if (request.getHeader("True-Client-Ip") != null) return request.getHeader("True-Client-Ip");
        if (request.getHeader("X-Forwarded-For") != null) return request.getHeader("X-Forwarded-For").split(",")[0];
        if (request.getHeader("HTTP_CF_CONNECTING_IP") != null) return request.getHeader("HTTP_CF_CONNECTING_IP");
        if (request.getHeader("CF-Connecting-IP") != null) return request.getHeader("CF-Connecting-IP");
        return request.getRemoteAddr();
    }

    public static Long ipToLong(String ip) {
        ip = isIPV4(ip) ? ip : "127.0.0.1";
        String ipParts[] = ip.split("\\.");
        return Long.valueOf(ipParts[3]) + (Long.valueOf(ipParts[2]) * 256) + (Long.valueOf(ipParts[1]) * 256 * 256)
                + (Long.valueOf(ipParts[0]) * 256 * 256 * 256);
    }

    private static boolean isIPV4(String ip) {
        return ip.matches("(^\\d{1,3})(\\.)(\\d{1,3})(\\.)(\\d{1,3})(\\.)(\\d{1,3}$)");
    }

    public static String longToIp(long ip) {
        StringBuilder sb = new StringBuilder(15);
        for (int i = 0; i < 4; i++) {
            sb.insert(0, Long.toString(ip & 0xff));
            if (i < 3) {
                sb.insert(0, '.');
            }
            ip >>= 8;
        }
        return sb.toString();
    }
}
