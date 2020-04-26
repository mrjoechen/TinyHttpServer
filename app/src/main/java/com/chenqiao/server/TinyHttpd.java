package com.chenqiao.server;

import android.os.Handler;
import android.os.Looper;
import android.util.JsonWriter;
import android.util.Log;

import com.chenqiao.handler.AbstractHandler;
import com.chenqiao.handler.DefaultHandler;
import com.chenqiao.handler.HttpdHandler;
import com.chenqiao.handler.TestHandler;
import com.chenqiao.util.StringUtils;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
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
        //注册handler
        registerHandler(TestHandler.class);
    }

    @Override
    public Response serve(IHTTPSession session) {

        Response response = newFixedLengthResponse("welcome to nps build by chenqiao!");
        response.addHeader("Access-Control-Allow-Headers", "authorization");
        response.addHeader("Access-Control-Allow-Origin", "*");
        Log.d(TAG, "--------------------------------------");

        String remoteHostName = session.getRemoteHostName();
        Log.d(TAG, remoteHostName);
        String remoteIpAddress = session.getRemoteIpAddress();
        Log.d(TAG + "-remoteIp", remoteIpAddress);
        final String uri = session.getUri();
        Method method = session.getMethod();
        Log.d(TAG + "-method", method.name());
        Log.d(TAG + "-uri", uri);
        Map<String, List<String>> parameters = session.getParameters();
        Log.d(TAG + "-param", parameters.toString());
        String queryParameterString = session.getQueryParameterString();
        Log.d(TAG + "-queryParam", StringUtils.isEmpty(queryParameterString) ? "null" : queryParameterString);

        //注：只能读取一次
//        InputStream inputStream = session.getInputStream();
//        String s = "";
//        try {
//            s = StringUtils.parseInputStreamToString(inputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Log.d(TAG + "-body", s);


        final Map<String, Object> result = new HashMap<>();

        String body = null;
        if (Method.POST == method) {
            body = getPostData(session);
        }

        Log.d(TAG + "-body", body+"");
        Log.d(TAG, "--------------------------------------");

        String name;
        int slash = uri.indexOf("/");
        name = (slash > 0 ? uri.substring(0, slash) : uri.toString()).toLowerCase().substring(1, uri.length());

        AbstractHandler handler = mHandlerMap.get(name);
        if (handler != null) {
            try {
                response = handler.handle(name, session, body, result);
            } catch (HttpdException e) {
                e.printStackTrace();
                AbstractHandler.putResponse(result, e.errCode, e.errMsg);
            }
        } else {
            try {
                response = DefaultHandler.getInstance().handle(name, session, body, result);
            } catch (HttpdException e) {
                e.printStackTrace();
            }
        }

        if (response == null) {

            JSONObject jsonObject = new JSONObject(result);
            String msg = jsonObject.toString();
            response = newFixedLengthResponse(msg);
            response.addHeader("Access-Control-Allow-Origin", "*");
            Log.i(TAG, msg);
        }


        // 浏览器请求单独请求 网页icon 过滤此请求
        if (!StringUtils.isEmpty(uri) && !"/favicon.ico".equals(uri)){
            if (mOnServListener != null){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mOnServListener.onServe(new JSONObject(result).toString());
                    }
                });
            }
        }
        return response;

    }

    private void registerHandler(Class<? extends AbstractHandler> handlerClazz) {
        HttpdHandler annotation = handlerClazz.getAnnotation(HttpdHandler.class);
        if (annotation != null) {
            String name = annotation.name();
            if (StringUtils.isNotEmpty(name)) {
                try {
//                    Constructor declaredConstructor = handlerClazz.getDeclaredConstructor();
//                    declaredConstructor.setAccessible(true);
                    AbstractHandler handlerInstance = handlerClazz.newInstance();
                    mHandlerMap.put(name, handlerInstance);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
//                } catch (NoSuchMethodException e) {
//                    e.printStackTrace();
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


    public boolean getServiceStatus(){
        return TINY_SERVER.isAlive();
    }
}
