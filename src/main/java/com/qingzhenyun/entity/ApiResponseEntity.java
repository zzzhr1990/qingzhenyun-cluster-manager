package com.qingzhenyun.entity;

import lombok.Data;

/**
 * HH
 * Created by guna on 2017/5/9.
 */
@Data
public class ApiResponseEntity {
    private boolean success = true;
    private int code = 200;
    private String message = "OK";
    private Object data;
}
