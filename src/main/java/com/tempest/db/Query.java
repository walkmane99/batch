package com.tempest.db;


import com.tempest.sql.system.ConnectionPool;

import java.sql.Connection;
import java.util.function.Consumer;

/**
 * 関数で利用したい。
 *
 * 今まで　
 * 　DBクエリ発行　＞　ORMでDAOに取り込みListにする　＞　コネクションを返す、　> アプリでListを利用
 *
 * 目指すばしょ
 * 　クエリ発行　＞　戻り値毎に処理する関数を動かす（アプリで利用）　＞　コネクションを返す。
 *
 *
 * Qurty q = new Query(<<SQL>>);
 * q.execute();  // update insert
 * q.execute(DAO.class);
 * //selectの時だけ
 * q.forEach(r->{});
 * q.map(r->{}).toList();
 *
 */
public class Query {

    private String query;

    public Query(String query) {
        this.query = query;
        // クエリの解析？
    }

    public <T> Result<T> execute(Class<T> clazz) {
        Result<T> result =  new Result<>(clazz);
        result.setQuery(this.query);
        return result;
    }

}
