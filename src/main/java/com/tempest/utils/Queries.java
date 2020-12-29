package com.tempest.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
/**
 * クエリ
 */
public interface Queries<T> {
    /**
     * 検索します。
     * @param con DBコネクション
     * @return 検索結果
     */
    public List<T> find(Connection con) throws SQLException;

}
