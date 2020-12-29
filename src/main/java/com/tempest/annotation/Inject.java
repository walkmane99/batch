package com.tempest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * フィールド（インスタンス変数）を注釈するアノテーションです。
 * 
 * 後でインスタンスを流し込むためのフィールドを特定するためのアノテーションです。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Inject {
    public static enum Type {
        VALUE, KEY;
    }

    Type type() default Type.VALUE;

}