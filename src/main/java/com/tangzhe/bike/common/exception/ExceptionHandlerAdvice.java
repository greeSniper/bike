package com.tangzhe.bike.common.exception;

import com.tangzhe.bike.common.constants.Constants;
import com.tangzhe.bike.common.resp.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by 唐哲
 * 2018-05-28 19:42
 * 全局异常处理
 */
@ControllerAdvice
@ResponseBody
@Slf4j
public class ExceptionHandlerAdvice {

    /**
     * 处理系统异常
     */
    @ExceptionHandler(Exception.class)
    public ApiResult handlerException(Exception e) {
        log.error(e.getMessage(), e);
        return new ApiResult(Constants.RESP_STATUS_INTERNAL_ERROR, "系统异常，请稍后再试");
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BikeException.class)
    public ApiResult handlerException(BikeException e) {
        log.error(e.getMessage(), e);
        return new ApiResult(e.getStatusCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult handleIllegalParamException(MethodArgumentNotValidException e) {
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        String message = "参数不合法";
        if (errors.size() > 0) {
            message = errors.get(0).getDefaultMessage();
        }
        ApiResult result = new ApiResult(Constants.RESP_STATUS_BADREQUEST, message);
        return result;
    }

}
