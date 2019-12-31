package com.xwapi.test;

import com.alibaba.fastjson.JSON;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Created by chenxuesong on 19/12/14.
 */
public class ApiTestForwardHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "hello world";
        httpExchange.sendResponseHeaders(200, 0);
        InputStream is = httpExchange.getRequestBody();
        String url = httpExchange.getRequestURI().toString();
        byte[] bytes = new byte[is.available()];
        is.read(bytes);
        String str = new String(bytes);
        System.out.println("read post body " + str);
        try {
            Map maps = (Map) JSON.parse(str);
            for (Object map : maps.entrySet()) {
                System.out
                    .println("key: " + ((Map.Entry) map).getKey() + "     value: " + ((Map.Entry) map).getValue());
            }
            response = JSON.toJSONString(maps);
        } catch (Exception e) {
            e.printStackTrace();
        }
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();

    }
}
