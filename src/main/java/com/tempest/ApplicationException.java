package com.tempest;

public class ApplicationException extends Exception {

    private static final long serialVersionUID = 1L;
    private int code = 1;

    public ApplicationException(Throwable e) {
        super(e);
    }

    public ApplicationException(Throwable e, int code) {
        super(e);
        this.code = code;
    }

    public ApplicationException(String massage) {
        super(massage);
    }

    public ApplicationException(String massage, Throwable e) {
        super(massage, e);
    }

    public ApplicationException(String massage, int code) {
        super(massage);
        this.code = code;
    }

    public ApplicationException(String massage, Throwable e, int code) {
        super(massage, e);
        this.code = code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        String message = super.toString();
        StringBuilder buff = new StringBuilder(message);
        buff.append(", Code=").append(getCode());
        return buff.toString();
    }
}