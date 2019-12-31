package com.flysands.xposeddecode;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

/**
 * Created by chenxuesong on 2019/12/31.
 */

public class ModuleDecode implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.flysands.mockapp")) {
            XposedBridge.log("ready to hook method.");
            XposedHelpers.findAndHookMethod("com.flysands.mockapp.EncryptUtil", lpparam.classLoader,
                                            "encryptDataWithKey", String.class, String.class,
                                            new XC_MethodHook() {
                                                @Override
                                                protected void beforeHookedMethod(
                                                    final MethodHookParam param) throws Throwable {
                                                    super.beforeHookedMethod(param);
                                                    final String org = (String) param.args[0];
                                                    XposedBridge.log("org data is " + org);
                                                    String
                                                        forwardServer =
                                                        "http://172.20.10.4:8088/apiforward";
                                                    InetSocketAddress
                                                        addr =
                                                        new InetSocketAddress("172.20.10.4", 8080);
                                                    final Proxy
                                                        proxy =
                                                        new Proxy(Proxy.Type.HTTP, addr);
                                                    final URL url = new URL(forwardServer);
                                                    Thread th = new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            HttpURLConnection connection =
                                                                null;
                                                            try {
                                                                connection = (HttpURLConnection) url
                                                                    .openConnection(proxy);
                                                                connection.setRequestMethod("POST");
                                                                connection.setDoOutput(true);
                                                                connection.getOutputStream()
                                                                    .write(org.getBytes());
                                                                int
                                                                    responseCode =
                                                                    connection.getResponseCode();
                                                                if (responseCode
                                                                    == HttpURLConnection.HTTP_OK) {
                                                                    InputStream
                                                                        inputStream =
                                                                        connection.getInputStream();
                                                                    String
                                                                        result =
                                                                        readStream(inputStream);
                                                                    XposedBridge.log(
                                                                        "get result from server "
                                                                        + result);
                                                                    param.args[0] = result;
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                    th.start();
                                                    th.join();
                                                }
                                            });
        }
    }

    public static String readStream(InputStream in) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len = -1;
        byte[] buffer = new byte[1024]; //1kb
        while ((len = in.read(buffer)) != -1) {

            baos.write(buffer, 0, len);
        }
        in.close();
        String content = new String(baos.toByteArray());

        return content;

    }
}
