package com.tempest.utils;

/**
 * 指定されたテーブルの状態(新規レコードの追加)を監視します。
 * 状態に変化があった場合、登録されているすべてのリスナにイベントを発行します。
 */
public interface Observer<T> {

    /**
     * リスナーを登録します。
     */
    void addListener(Listener<T> listener);
    /**
     * リスナーを削除します。
     */
    void removeListener(Listener<T> listener);
}
