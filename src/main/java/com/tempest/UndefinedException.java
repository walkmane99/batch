
package com.tempest;

public class UndefinedException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public UndefinedException(Exception e) {
        super(e);
    }

    public UndefinedException(String msg) {
        super(msg);
    }

    public UndefinedException(String msg, Exception e) {
        super(msg, e);
    }
}