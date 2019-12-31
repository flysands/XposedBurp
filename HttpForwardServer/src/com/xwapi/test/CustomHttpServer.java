package com.xwapi.test;

import com.sun.net.httpserver.HttpServer;

import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by chenxuesong on 19/12/14.
 */
public class CustomHttpServer {

    private HttpServer mServer;

    public CustomHttpServer(int port) {
        try {
            mServer = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setHandler(String path, HttpHandler handler) {
        mServer.createContext(path, handler);
    }

    public void startForward() {
        mServer.start();
    }

}
