package com.chenqiao.server;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.chenqiao.handler.AbstractHandler;
import com.chenqiao.handler.HttpdHandler;
import com.chenqiao.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by chenqiao on 2018/7/24.
 */

public class TinyHttpd extends NanoHTTPD {

    private static final String TAG = "TinyHttpd";

    private static final String TINY_SERVER_IP = "0.0.0.0";
    private static final int TINY_SERVR_PORT = 8234;

    private static final TinyHttpd TINY_SERVER = new TinyHttpd();

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public static TinyHttpd getInstance() {
        return TINY_SERVER;
    }
    private Map<String, AbstractHandler> mHandlerMap = new HashMap<>();



    private TinyHttpd() {
        super(TINY_SERVER_IP, TINY_SERVR_PORT);
    }

    @Override
    public Response serve(IHTTPSession session) {

        Response response = newFixedLengthResponse("welcome to ngrok build by chenqiao!");
        response.addHeader("Access-Control-Allow-Headers", "authorization");
        response.addHeader("Access-Control-Allow-Origin", "*");
        String remoteHostName = session.getRemoteHostName();
        final String uri = session.getUri();
        Log.d(TAG, remoteHostName);

        if (!StringUtils.isEmpty(uri) && !"/favicon.ico".equals(uri)){
            if (mOnServListener != null){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mOnServListener.onServe(uri);
                    }
                });
            }
        }
        Log.d(TAG, uri);
        return response;

    }

    private void registerHandler(Class<? extends AbstractHandler> handlerClazz) {
        HttpdHandler annotation = handlerClazz.getAnnotation(HttpdHandler.class);
        if (annotation != null) {
            String name = annotation.name();
            if (StringUtils.isNotEmpty(name)) {
                try {
                    AbstractHandler handlerInstance = handlerClazz.newInstance();
                    mHandlerMap.put(name, handlerInstance);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void startServer() {
        try {
            if (TINY_SERVER != null) {
                TINY_SERVER.start();
                Log.d(TAG, "TinyServer start");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void stopServer() {
        try {
            if (TINY_SERVER != null) {
                TINY_SERVER.stop();
                Log.d(TAG, "TinyServer stop");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getPostData(IHTTPSession session) {
        String body = null;
        try {
            Map<String, String> mapFile = new HashMap<>();
            session.parseBody(mapFile);
            body = mapFile.get("postData");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }

    public interface OnServListener{
        void onServe(String result);
    }

    private OnServListener mOnServListener;

    public void setOnserveListener(OnServListener onserveListener){
        mOnServListener = onserveListener;
    }
}
