package com.tempest.db;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

import com.tempest.utils.FaildCreateObjectException;

public interface Executable {
    /**
     * SQLを実行する。
     *
     * @param clazz    関数に戻す型
     * @param consumer 取得したデータを加工する関数
     * @param <T>      関数に戻す型の仮引数
     * @return
     * @throws SQLException
     * @throws FaildCreateObjectException
     */
    <T> int execute(Class<T> clazz, Consumer<T> consumer) throws SQLException, FaildCreateObjectException;

    /**
     * SQLを実行する。
     *
     * @param clazz 関数に戻す型
     * @param <T>   関数に戻す型の仮引数
     * @return
     * @throws SQLException
     * @throws FaildCreateObjectException
     */
    <T> List<T> execute(Class<T> clazz) throws SQLException, FaildCreateObjectException;

    /**
     * SQLを実行する。
     *
     * @param <T> 関数に戻す型の仮引数
     * @return
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws FaildCreateObjectException
     */
    <T> int execute(List<T> list) throws SQLException, IllegalAccessException;

}
