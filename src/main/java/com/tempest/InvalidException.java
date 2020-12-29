
package com.tempest;

public class InvalidException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public InvalidException(Exception e) {
        super(e);
    }

    public InvalidException(String msg) {
        super(msg);
    }

    public InvalidException(String msg, Exception e) {
        super(msg, e);
    }
}