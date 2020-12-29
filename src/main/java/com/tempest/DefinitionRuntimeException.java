package com.tempest;

/**
 * 定義情報に問題がある場合、スローされる例外です。
 */
public class DefinitionRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DefinitionRuntimeException(DefinitionException e) {
        super(e);
    }

    public DefinitionRuntimeException(String message, Exception e) {
        super(new DefinitionException(message, e));
    }
}