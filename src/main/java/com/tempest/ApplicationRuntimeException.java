package com.tempest;

/**
 * ApplicationException
 */
public class ApplicationRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    public ApplicationRuntimeException() {

    }
    public ApplicationRuntimeException(ApplicationException e) {
        super(e);
    }
}
