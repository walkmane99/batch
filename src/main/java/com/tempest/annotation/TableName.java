package com.tempest.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * モデルを注釈するアノテーションです。
 *
 * namesは配列を設定し、それそれのProcNoに応じたテーブル名を記述します。
 * keyにはnamesからテーブル名を取得するためのプロパティ名を設定します。デフォルトは"procNo"です。
 * keyに設定したプロパティからプロセス番号を取得します。 テーブル名の決定は、（取得したプロセス番号/10)
 * -1で添え字を決定しnamesから取得します。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TableName {
    String[] names();

    int[] keyValues() default { 10, 20, 30 };

    String key() default "procNo";
}
