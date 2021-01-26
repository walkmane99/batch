package com.tempest.db;

import com.tempest.sql.system.ConnectionPool;
import com.tempest.utils.FaildCreateObjectException;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 関数で利用したい。
 *
 * 今まで DBクエリ発行 ＞ ORMでDAOに取り込みListにする ＞ コネクションを返す、 > アプリでListを利用
 *
 * 目指すばしょ クエリ発行 ＞ 戻り値毎に処理する関数を動かす（アプリで利用） ＞ コネクションを返す。
 *
 *
 * Qurty q = new Query(<<SQL>>); q.execute(); // update insert
 * q.execute(DAO.class); //selectの時だけ q.forEach(r->{}); q.map(r->{}).toList();
 *
 * 値の渡し方、型が異なる。 数値 日付 文字列
 *
 *
 * オブジェクト (バイナリ）
 *
 */
public final class Query {

    private String query;

    private List<Condition<?>> conditions;

    public Query(String query) {
        this.conditions = new ArrayList<>();
        this.query = query;
    }

    List<Condition<?>> getConditions() {
        return this.conditions;
    }

    /**
     * 条件の値を登録します。
     *
     * @param name  名前
     * @param value 値
     * @return インスタンス
     */
    public Query append(String name, Double value) {
        this.conditions.add(new DoubleCondition(name, value));
        return this;
    }

    /**
     * 条件の値を登録します。
     *
     * @param name  名前
     * @param value 値
     * @return インスタンス
     */
    public Query append(String name, String value) {
        this.conditions.add(new StringCondition(name, value));
        return this;
    }

    /**
     * 条件の値を登録します。
     *
     * @param name  名前
     * @param value 値
     * @return インスタンス
     */
    public Query append(String name, LocalDate value) {
        this.conditions.add(new DateCondition(name, value));
        return this;
    }

    /**
     * 条件の値を登録します。
     *
     * @param name  名前
     * @param value 値
     * @return インスタンス
     */
    public Query append(String name, Integer value) {
        this.conditions.add(new IntCondition(name, value));
        return this;
    }

    /**
     * 条件の値を登録します。
     *
     * @param name  名前
     * @param value 値
     * @return インスタンス
     */
    public Query append(String name, Long value) {
        this.conditions.add(new LongCondition(name, value));
        return this;
    }

    /**
     * 条件の値を登録します。
     *
     * @param name  名前
     * @param value 値
     * @return インスタンス
     */
    public Query append(String name, List<?> value) {
        this.conditions.add(new ListCondition(name, value));
        return this;
    }

    String getSQL() {
        return this.query;
    }

    /**
     * SQLを実行する。 TODO: トランザクションをどうするか？
     *
     * @param clazz    関数に戻す型
     * @param consumer 取得したデータを加工する関数
     * @param <T>      関数に戻す型の仮引数
     * @return
     * @throws SQLException
     * @throws FaildCreateObjectException
     */
    public <T> int execute(Class<T> clazz, Consumer<T> consumer) throws SQLException, FaildCreateObjectException {
        Result<T> result = new Result<>(clazz);
        result.setQuery(this);
        try (Connection con = ConnectionPool.getInstance().getConnection()) {
            return result.execute(con, consumer);
        }
    }

    /**
     * SQLを実行する。 TODO: トランザクションをどうするか？
     *
     * @param clazz 関数に戻す型
     * @param <T>   関数に戻す型の仮引数
     * @return
     * @throws SQLException
     * @throws FaildCreateObjectException
     */
    public <T> List<T> execute(Class<T> clazz) throws SQLException, FaildCreateObjectException {
        Result<T> result = new Result<>(clazz);
        List<T> list = new ArrayList<>();
        result.setQuery(this);
        try (Connection con = ConnectionPool.getInstance().getConnection()) {
            result.execute(con, record -> list.add(record));
        }
        return list;
    }

    /**
     * SQLを実行する。 TODO: トランザクションをどうするか？
     *
     * @param <T> 関数に戻す型の仮引数
     * @return
     * @throws SQLException
     * @throws FaildCreateObjectException
     * @throws IllegalAccessException
     */
    public <T> int execute(List<T> list) throws SQLException, FaildCreateObjectException, IllegalAccessException {
        Result<T> result = new Result<>();
        result.setQuery(this);
        try (Connection con = ConnectionPool.getInstance().getConnection()) {
            return result.execute(con, list);
        }
    }

}
