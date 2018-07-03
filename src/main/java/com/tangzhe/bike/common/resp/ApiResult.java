package com.tangzhe.bike.common.resp;

import com.tangzhe.bike.common.constants.Constants;
import lombok.Data;

@Data
public class ApiResult<T> {

    private int code = Constants.RESP_STATUS_OK;

    private String message;

    private T data;

    public ApiResult() {}

    public ApiResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
