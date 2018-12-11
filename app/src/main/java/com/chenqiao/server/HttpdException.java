package com.chenqiao.server;

/**
 * Created by chenqiao on 2018/7/24.
 */

public class HttpdException extends Exception {
    final int errCode;
    final String errMsg;

    public HttpdException(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
}
