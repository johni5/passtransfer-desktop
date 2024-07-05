package com.del.passtransfer.utils;

import java.util.Optional;

/**
 * Created by DodolinEL
 * date: 03.07.2024
 */
public final class WSUtils {

    private WSUtils() {
    }

    public static String getHost() {
        return Optional.of(Utils.getInfo().getString("ws.host")).orElse("localhost").trim();
    }

    public static boolean isSecure() {
        return Optional.of(Utils.getInfo().getString("ws.https")).orElse("false").trim().equalsIgnoreCase("true");
    }

    public static String getHttpUrl() {
        if (isSecure()) {
            return String.format("https://%s", getHost());
        }
        return String.format("http://%s", getHost());
    }

    public static String getWsUrl() {
        if (isSecure()) {
            return String.format("wss://%s", getHost());
        }
        return String.format("ws://%s", getHost());
    }

}
