package com.tempest.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.tempest.annotation.Command;
import com.tempest.sql.*;

/**
 * SQLを実行するインターフェースです。
 */
public interface SQLExecutor {
    /**
     * 検索します。
     *
     * @param con DBコネクション
     * @param query　クエリ
     * @param model　モデル（値）
     * @param clazz  modelのクラス
     */
    @Command
    public <T> List<T> executor(Connection con, SelectQuery query, T model, Class<T> clazz) throws SQLException;

   /**
     * 検索します。
     *
     * @param con DBコネクション
     * @param query　クエリ
     * @param model　モデル（値）
     * @param clazz  modelのクラス
     */
    @Command
    public <T> int executor(Connection con, CountQuery query, T model, Class<T> clazz) throws SQLException;


    /**
     * 挿入します。
     *
     * @param con DBコネクション
     * @param query　クエリ
     * @param model　モデル（値）
     * @param clazz  modelのクラス
     */
    @Command
    public <T> int executor(Connection con, InsertQuery query, T model, Class<T> clazz) throws SQLException ;
    /**
     * 挿入します。
     *
     * @param con DBコネクション
     * @param query　クエリ
     * @param model　モデル（値）のリスト
     * @param clazz  modelのクラス
     */
    @Command
    public <T> int executor(Connection con, InsertQuery query, List<T> model, Class<T> clazz) throws SQLException ;

    /**
     * 挿入します。
     * SelectInsertを行います。
     *
     * @param con DBコネクション
     * @param query　クエリ
     * @param model　モデル（値）のリスト
     * @param clazz  modelのクラス
     */
    @Command
    public <T> int executor(Connection con, SelectInsertQuery query, T model, Class<T> clazz) throws SQLException ;

    /**
     * 削除します
     *
     * @param con DBコネクション
     * @param query　クエリ
     * @param model　モデル（値）
     * @param clazz  modelのクラス
     */
    @Command
    public <T> int executor(Connection con, DeleteQuery query, T model, Class<T> clazz) throws SQLException ;
    /**
     * 更新します。
     *
     * @param con DBコネクション
     * @param query　クエリ
     * @param model　モデル（値）
     * @param clazz  modelのクラス
     */
    @Command
    public <T> int executor(Connection con, UpdateQuery query, T model, Class<T> clazz) throws SQLException ;



}
