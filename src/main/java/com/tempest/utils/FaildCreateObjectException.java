package com.tempest.utils;

import com.tempest.ApplicationException;

public class FaildCreateObjectException extends ApplicationException {

    public  FaildCreateObjectException(String message) {
        super(message);
    }

    public  FaildCreateObjectException(String message, Exception e) {
        super(message, e);
    }
    public FaildCreateObjectException( Throwable e) {
        super(e);
    }


}
