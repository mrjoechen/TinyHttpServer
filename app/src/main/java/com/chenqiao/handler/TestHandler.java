package com.chenqiao.handler;

import android.os.Build;
import android.util.Log;

import com.chenqiao.App;
import com.chenqiao.server.HttpdException;
import com.chenqiao.tinyhttpserver.BuildConfig;
import com.chenqiao.util.DeviceUtils;
import com.chenqiao.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by chenqiao on 2019-12-27.
 * e-mail : mrjctech@gmail.com
 */

@HttpdHandler(name = "test")
public class TestHandler extends AbstractHandler {

    private static final String TAG = "TestHandler";


    @Override
    public NanoHTTPD.Response handle(String name, NanoHTTPD.IHTTPSession session, String body, Map<String, Object> result) throws HttpdException {

        Log.d(TAG, "--------------------------------------");

        String remoteHostName = session.getRemoteHostName();
        Log.d(TAG, remoteHostName);
        String remoteIpAddress = session.getRemoteIpAddress();
        Log.d(TAG + "-remoteIp", remoteIpAddress);
        String uri = session.getUri();
        NanoHTTPD.Method method = session.getMethod();
        Log.d(TAG + "-method", method.name());
        Log.d(TAG + "-uri", uri);
        Map<String, List<String>> parameters = session.getParameters();
        Log.d(TAG + "-param", parameters.toString());
        String queryParameterString = session.getQueryParameterString();
        Log.d(TAG + "-queryParam", StringUtils.isEmpty(queryParameterString) ? "null" : queryParameterString);

        InputStream inputStream = session.getInputStream();
        String s = "";
        try {
            s = StringUtils.parseInputStreamToString(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG + "-body", s);

        Log.d(TAG, "--------------------------------------");


        result.put("remoteHostName", remoteHostName);
        result.put("remoteIpAddress", remoteIpAddress);
        result.put("method", method.name());
        result.put("uri", uri);
        result.put("param", parameters.toString());
        result.put("queryParam", StringUtils.isEmpty(queryParameterString) ? "null" : queryParameterString);
        result.put("device", DeviceUtils.getSystemModel());
        result.put("systemVersion", DeviceUtils.getSystemVersion());
        result.put("systemDevice", DeviceUtils.getSystemDevice());
        result.put("deviceBrand", DeviceUtils.getDeviceBrand());
        result.put("manufacturer", DeviceUtils.getDeviceManufacturer());
        result.put("deviceBoard", DeviceUtils.getDeviceBoard());
        result.put("language", DeviceUtils.getSystemLanguage());
        result.put("IMEI", DeviceUtils.getIMEI(App.getInstance()));
        result.put("serverVersionCode", BuildConfig.VERSION_CODE);
        result.put("serverVersionName", BuildConfig.VERSION_NAME);

        return null;
    }
}
