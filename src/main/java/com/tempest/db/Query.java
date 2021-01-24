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
public class Query {

    private String query;

    private List<Condition<?>> conditions;

    public Query(String query) {
        this.conditions = new ArrayList<>();
        this.query = query;
        // クエリの解析？
        // select と その他は違う
    }

    public Query append(String name, String value) {
        this.conditions.add(new StringCondition(name, value));
        return this;
    }

    public Query append(String name, LocalDate value) {
        this.conditions.add(new DateCondition(name, value));
        return this;
    }

    public Query append(String name, Integer value) {
        this.conditions.add(new IntCondition(name, value));
        return this;
    }

    public Query append(String name, Long value) {
        this.conditions.add(new LongCondition(name, value));
        return this;
    }

    String getSQL() {
        return this.query;
    }

    public List<Condition<?>> getConditions() {
        return this.conditions;
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
    public <T> Result<T> execute(Class<T> clazz, Consumer<T> consumer) throws SQLException, FaildCreateObjectException {
        Result<T> result = new Result<>(clazz);
        result.setQuery(this);
        try (Connection con = ConnectionPool.getInstance().getConnection()) {
            result.execute(con, consumer);
        }
        return result;
    }

}
