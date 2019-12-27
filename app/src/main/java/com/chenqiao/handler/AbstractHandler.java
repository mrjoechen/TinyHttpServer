package com.chenqiao.handler;

import android.util.Log;

import com.chenqiao.server.HttpdException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by chenqiao on 2018/7/24.
 */

public abstract class AbstractHandler {
    /**
     * <p>处理请求, 此方法应当是线程安全的</p>
     * <p>若返回为null,则将result转换为结果作为响应输出</p>
     * <p>若返回不为null,则直接将返回值作为响应输出</p>
     * <p>若抛出异常，则将根据异常输出响应</p>
     * @param name
     * @param session
     * @param body
     * @param result
     * @return
     */
    public abstract NanoHTTPD.Response handle(String name, NanoHTTPD.IHTTPSession session, String body, Map<String, Object> result) throws HttpdException;

    public static void putResponse(Map<String, Object> response, int code, String msg) {
        response.put("errcode", code);
        response.put("errmsg", msg);
    }
}
