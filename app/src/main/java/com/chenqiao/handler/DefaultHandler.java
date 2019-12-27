package com.chenqiao.handler;


import android.util.Log;

import com.chenqiao.server.HttpdException;
import com.chenqiao.util.StringUtils;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * 默认Handler，输出欢迎信息
 * Created by chenqiao on 2018/7/24.
 */

public class DefaultHandler extends AbstractHandler {

    private static final int RESPONSE_CODE_WELCOME = 100;
    private static final int RESPONSE_CODE_UNSUPPORTED_ACTION = 18;

    private static final String RESPONSE_MSG_WELCOME = "Welcome to visit TinyHttpd!";

    private static final String RESPONSE_MSG_UNSUPPORTED_ACTION = "暂不支持此操作";

    private static final String TAG = "DefaultHandler";
    private static class Holder {
        private static DefaultHandler sInstance = new DefaultHandler();
    }

    private DefaultHandler() {
    }

    public static DefaultHandler getInstance() {
        return Holder.sInstance;
    }

    @Override
    public NanoHTTPD.Response handle(String name, NanoHTTPD.IHTTPSession session, String body, Map<String, Object> result) throws HttpdException {

        String queryParameterString = session.getQueryParameterString();
        Log.d(TAG, "queryParameterString：" + queryParameterString);
        Log.d(TAG, "getRemoteHostName：" + session.getRemoteHostName());
        Log.d(TAG, "getRemoteIpAddress：" + session.getRemoteIpAddress());

        result.put("getRemoteHostName", session.getRemoteHostName());
        result.put("getRemoteIpAddress", session.getRemoteIpAddress());
        result.put("time", StringUtils.getCompleteTimeString());
        if (StringUtils.isEmpty(name)) {
            putResponse(result, RESPONSE_CODE_WELCOME, RESPONSE_MSG_WELCOME);
        } else {
            putResponse(result, RESPONSE_CODE_UNSUPPORTED_ACTION, RESPONSE_MSG_UNSUPPORTED_ACTION);
        }
        return null;
    }
}