package com.tempest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定義ファイル作成クラス用のアノテーション
 * 
 * マーカーとしてしか機能しません。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DefineCreator {
    Class<?> type();
}