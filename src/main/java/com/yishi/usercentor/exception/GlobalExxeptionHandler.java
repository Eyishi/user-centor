package com.yishi.usercentor.exception;

import com.yishi.usercentor.common.BaseResponse;
import com.yishi.usercentor.common.ErrorCode;
import com.yishi.usercentor.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/***
 * 全局异常处理  ，
 */
@Slf4j
@RestControllerAdvice
public class GlobalExxeptionHandler {
    @ExceptionHandler(BusinessException.class) //针对这个异常  捕获这个异常
    public BaseResponse businessExceptionHandler(BusinessException e){
        log.error("businessException："+e.getMessage(),e);
        return ResultUtils.error(e.getCode(),e.getMessage(),e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e){
        log.error("runtimeexception："+e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,e.getMessage(),"");
    }
}
