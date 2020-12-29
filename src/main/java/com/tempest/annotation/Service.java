package com.tempest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * DBにアクセスするクラスにつけておくアノテーション。
 *
 * マーカーとしてしか機能しません。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Service {
    public static enum Proxy {
        ON, OFF;
    }

    Proxy proxy() default Proxy.OFF;
}
