package com.tempest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * フィールド（インスタンス変数）を注釈するアノテーションです。
 *
 * 後でインスタンスを流し込むためのフィールドを特定するためのアノテーションです。
 * typeを指定すると、指定したクラスをインスタンス化して流し込みます。
 * （インターフェースが注釈先の型に指定している場合必須です。　java.sql.Connectionはのぞく）
 *
 * 指定しない場合は、注釈先の型をインスタンス化して流し込みます。
 * 注釈先の型がjava.sql.Connectionの場合は、ConnectionPoolからConnectionを生成して流し込みます。
 * （Transactionクラスが必要です）
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Autowired {
    Class<?> type() default  Object.class;
    String name() default "";
}
