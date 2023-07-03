package com.heima.common.exception;

import com.heima.model.common.enums.AppHttpCodeEnum;

/**
 * Description: 自定义的异常类，也就是手动抛出的异常
 * Class Name: CustomException
 * Date: 2023/7/2 17:58
 *
 * @author Hao
 * @version 1.1
 */
public class CustomException extends RuntimeException {

    private AppHttpCodeEnum appHttpCodeEnum;

    public CustomException(AppHttpCodeEnum appHttpCodeEnum){
        this.appHttpCodeEnum = appHttpCodeEnum;
    }

    public AppHttpCodeEnum getAppHttpCodeEnum() {
        return appHttpCodeEnum;
    }
}
