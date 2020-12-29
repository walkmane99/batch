package com.tempest;

import java.lang.reflect.Field;
import java.util.stream.Stream;

/**
 * Hoge
 */
public class Hoge {

    public void parsing(Object obj) {

        /*
         * オブジェクトから、各種アノテーションを取り出す。 アノテーションに対する処理を実行する
         */
    }

    /**
     * 全てのフィールドを取得して、アノテーションの有無を調べる アノテーションの種類によって異なる処理をおこなう
     * 
     * @param obj アノテーション付きフィールドを探すインスタンス
     */
    public void parsingField(Object obj) {
        // オブジェクトの親クラスを精査する。
        Field[] fields = getFields(obj.getClass());
        // 各フィールドについているアノテーションを確認
        Stream.of(fields).flatMap(field -> Stream.of(field.getAnnotations())).forEach(annotation -> {

        });
    }

    /**
     * クラスが持つすべてのフィールドを返します。
     * 
     * @param clazz クラス
     * @return スーパークラスが持つものを合わせたすべてのフィールド
     */
    public Field[] getFields(Class<?> clazz) {
        Field[] parentField = null;
        clazz = clazz.getSuperclass();
        if (clazz != null) {
            parentField = getFields(clazz);
        }
        Field[] crrentField = clazz.getDeclaredFields();
        Field[] field = new Field[parentField.length + crrentField.length];
        System.arraycopy(parentField, 0, field, 0, parentField.length);
        System.arraycopy(crrentField, 0, field, parentField.length - 1, crrentField.length);
        return field;
    }

}