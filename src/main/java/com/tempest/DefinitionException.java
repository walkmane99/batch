package com.tempest;

/**
 * 定義情報に問題がある場合、スローされる例外です。
 */
public class DefinitionException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public DefinitionException(Exception e) {
        super(e);
    }

    public DefinitionException(String msg) {
        super(msg);
    }

    public DefinitionException(String msg, Exception e) {
        super(msg, e);
    }
}