package com.del.passtransfer.utils;

import com.del.passtransfer.ConnectionListener;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import org.apache.log4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * Created by DodolinEL
 * date: 03.07.2024
 */
public class ConnectionManager {

    final static private Logger logger = Logger.getLogger(ConnectionManager.class);

    private ConnectionListener listener;
    private String sid;

    public ConnectionManager(ConnectionListener listener) {
        this.listener = listener;
    }

    public String getSid() {
        return sid;
    }

    public void connect() {
        sid = UUID.randomUUID().toString();
        String url = String.format("%s/%s/%s", WSUtils.getHttpUrl(), "connect", sid);
        logger.info("Get: " + url);
        HttpRequest request = HttpRequest.newBuilder().
                uri(URI.create(url)).
                timeout(Duration.ofSeconds(10)).
                GET().build();
        HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).
                thenAccept(r -> {
                    if (!Strings.isNullOrEmpty(r.body())) {
                        listener.connected(r.body());
                    } else {
                        listener.error("Empty result");
                    }
//            if (r.statusCode() == 200) {
//                listener.connected(r.body());
//            } else {
//                listener.error(String.format("Code: %s -> %s", r.statusCode(), r.body()));
//            }
                }).whenComplete((v, t) -> {
            if (t != null) {
                listener.error("Exception: " + t.getMessage());
            }
        });
    }

}
