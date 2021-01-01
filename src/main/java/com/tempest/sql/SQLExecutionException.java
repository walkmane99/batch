package com.tempest.sql;

import com.tempest.ApplicationException;

/**
 * SQLExecutionException
 */
public class SQLExecutionException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    private static final int ERROR_CODE = 1;

    public SQLExecutionException(Throwable e) {
        super(e);
        this.writeCode();
    }

    public SQLExecutionException(String massage) {
        super(massage);
        this.writeCode();
    }

    public SQLExecutionException(String massage, Throwable e) {
        super(massage, e);
        this.writeCode();
    }

    private void writeCode() {
        int code = this.getCode();
        this.setCode(code + ERROR_CODE);
    }
}
