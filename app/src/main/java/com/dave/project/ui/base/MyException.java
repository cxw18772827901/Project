package com.dave.project.ui.base;

/**
 * 自定义异常
 * Created by Dave on 2016/12/22.
 */

public class MyException extends RuntimeException {
    public MyException() {
    }

    public MyException(String detailMessage) {
        super(detailMessage);
    }

    public MyException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public MyException(Throwable throwable) {
        super(throwable);
    }
}
