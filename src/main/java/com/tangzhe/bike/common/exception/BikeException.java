package com.tangzhe.bike.common.exception;

import com.tangzhe.bike.common.constants.Constants;

/**
 * 自定义业务异常
 */
public class BikeException extends RuntimeException{

    private int statusCode = Constants.RESP_STATUS_INTERNAL_ERROR;

    public BikeException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
    public BikeException(String message) {
        super(message);
    }

    public int getStatusCode() {
        return statusCode;
    }

}
