package com.xwapi.test;

public class Main {

    public static void main(String[] args) {
	// write your code here
        CustomHttpServer httpServer = new CustomHttpServer(8088);
        httpServer.setHandler("/apiforward", new ApiTestForwardHandler());
        httpServer.startForward();
    }
}
