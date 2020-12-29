package com.tempest;

public class InvokeException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public InvokeException(Exception e) {
        super(e);
    }

    public InvokeException(String msg) {
        super(msg);
    }

    public InvokeException(String msg, Exception e) {
        super(msg, e);
    }
}