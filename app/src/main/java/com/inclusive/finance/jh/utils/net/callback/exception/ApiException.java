package com.inclusive.finance.jh.utils.net.callback.exception;

import com.google.gson.annotations.SerializedName;

/**
 * Created by librabin on 16/10/9.
 */

public class ApiException extends RuntimeException {
    @SerializedName("message")
    private final String errMsg;
    private final int errCode;
    private String errData;


    public ApiException(int code, String msg) {
        super(msg);
        this.errCode = code;
        this.errMsg = msg;
    }

    public ApiException(int code, String msg, String errorData) {
        super(msg);
        this.errCode = code;
        this.errMsg = msg;
        this.errData = errorData;
    }


    public int getErrCode() {
        return errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public String getErrData() {
        return errData;
    }


    @Override
    public String toString() {
        return "ApiException{" +
                "errMsg='" + errMsg + '\'' +
                ", errCode=" + errCode +
                ", errData='" + errData + '\'' +
                '}';
    }
}
