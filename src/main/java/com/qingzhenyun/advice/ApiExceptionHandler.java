package com.qingzhenyun.advice;

import com.qingzhenyun.entity.ApiResponseEntity;
import com.qingzhenyun.exception.ApiException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Exception
 * Created by guna on 2017/5/10.
 */
@ControllerAdvice(basePackages = "com.qingzhenyun.controller.api")
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({ApiException.class})
    protected ResponseEntity<Object> handleInvalidRequest(ApiException e, WebRequest request) {
        ApiResponseEntity ar = new ApiResponseEntity();
        ar.setCode(e.getCode());
        ar.setMessage(e.getMessage());
        ar.setSuccess(false);
        return handleExceptionInternal(e, ar, new HttpHeaders(), HttpStatus.valueOf(ar.getCode()), request);
    }
}
