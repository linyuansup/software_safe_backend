
package com.honkai.blog.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class CustomException extends RuntimeException {

    
    private int code;

    
    private String msg;

    
    private Object data;

    public CustomException(String msg) {
        this.code = 400;
        this.msg = msg;
    }

    public CustomException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public CustomException(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

}