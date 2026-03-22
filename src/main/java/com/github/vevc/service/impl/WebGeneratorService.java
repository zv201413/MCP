package com.github.vevc.service.impl;

import com.github.vevc.config.AppConfig;
import com.github.vevc.util.LogUtil;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class WebGeneratorService {

    private HttpServer server;
    private final int port;
    private volatile boolean stopping = false;

    public WebGeneratorService(int port) {
        this.port = port;
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", rootHandler());
            server.setExecutor(Executors.newSingleThreadExecutor());
            server.start();
            LogUtil.info("[WebGen] Config generator started at http://0.0.0.0:" + port);
        } catch (IOException e) {
            LogUtil.error("[WebGen] Failed to start HTTP server on port " + port, e);
        }
    }

    private HttpHandler rootHandler() {
        return exchange -> {
            if (stopping) return;

            String path = exchange.getRequestURI().getPath();
            if (!path.equals("/") && !path.endsWith(".html")) {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }

            try (InputStream is = getClass().getResourceAsStream("/web-generator/index.html")) {
                if (is == null) {
                    String notFound = "Web generator not found in jar".getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(404, notFound.length);
                    exchange.getResponseBody().write(notFound);
                    exchange.close();
                    return;
                }

                byte[] html = is.readAllBytes();
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
                exchange.sendResponseHeaders(200, html.length);
                exchange.getResponseBody().write(html);
                exchange.close();
            } catch (IOException e) {
                LogUtil.error("[WebGen] Error serving page", e);
                exchange.sendResponseHeaders(500, -1);
                exchange.close();
            }
        };
    }

    public void stop() {
        stopping = true;
        if (server != null) {
            server.stop(1);
            LogUtil.info("[WebGen] HTTP server stopped");
        }
    }
}
