package com.tempest.sql;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.tempest.annotation.Command;
import com.tempest.utils.ReflectionUtils;
import com.tempest.utils.DateUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * SQLを実行するインターフェースです。
 */
// @Component
public final class SystemSQLExecutor implements SQLExecutor {

    private SQLExecutor db;
    private Config conf;

    public SystemSQLExecutor(SQLExecutor db) {
        this(db, ConfigFactory.load());
    }

    public SystemSQLExecutor(SQLExecutor db, Config conf) {
        this.db = db;
        this.conf = conf;
    }

    /**
     * 検索します。
     *
     * @param con   DBコネクション
     * @param query クエリ
     * @param model モデル（値）
     * @param clazz modelのクラス
     */
    @Command
    public <T> List<T> executor(Connection con, SelectQuery query, T model, Class<T> clazz) throws SQLException {
        return this.db.executor(con, query, model, clazz);
    }

    /**
     * 検索します。
     *
     * @param con   DBコネクション
     * @param query クエリ
     * @param model モデル（値）
     * @param clazz modelのクラス
     */
    @Command
    public <T> int executor(Connection con, CountQuery query, T model, Class<T> clazz) throws SQLException {
        return this.db.executor(con, query, model, clazz);
    }

    /**
     * 挿入します。
     *
     * @param con   DBコネクション
     * @param query クエリ
     * @param model モデル（値）
     * @param clazz modelのクラス
     */
    @Command
    public <T> int executor(Connection con, InsertQuery query, T model, Class<T> clazz) throws SQLException {
        if (this.conf.hasPath("system.moduleId")) {
            List<Field> fields = this.getField(clazz);
            if (fields.size() > 0) {
                // そもそもClassに存在しない場合は追加してはいけない
                // CREATED, CREATED_BY, UPDATED, UPDATEED_BYを、SETの条件に追加する。
                List<String> columns = Arrays.asList("CREATED", "CREATED_BY", "UPDATED", "UPDATED_BY");
                List<String> newList = new ArrayList<>();
                List<String> list = query.getColumns();
                newList.addAll(list.stream().map(String::toUpperCase).collect(Collectors.toList()));
                columns.stream().filter(str -> !newList.contains(str)).forEach(str -> newList.add(str));
                query.setColumns(newList);
                this.set(model, fields);
            }
        }
        return this.db.executor(con, query, model, clazz);
    }

    /**
     * 挿入します。
     *
     * @param con   DBコネクション
     * @param query クエリ
     * @param model モデル（値）のリスト
     * @param clazz modelのクラス
     */
    @Command
    public <T> int executor(Connection con, InsertQuery query, List<T> model, Class<T> clazz) throws SQLException {
        if (this.conf.hasPath("system.moduleId")) {
            // そもそもClassに存在しない場合は追加してはいけない
            List<Field> fields = this.getField(clazz);
            if (fields.size() > 0) {
                // CREATED, CREATED_BY, UPDATEED, UPDATEED_BYを、SETの条件に追加する。
                List<String> columns = Arrays.asList("CREATED", "CREATED_BY", "UPDATED", "UPDATED_BY");
                List<String> newList = new ArrayList<>();
                List<String> list = query.getColumns();
                newList.addAll(list.stream().map(str -> str.toUpperCase()).collect(Collectors.toList()));
                columns.stream().filter(str -> !newList.contains(str)).forEach(str -> newList.add(str));
                query.setColumns(newList);
            }
            for (T m : model) {
                this.set(m, fields);
            }
        }
        return this.db.executor(con, query, model, clazz);
    }

    /**
     * 挿入します。 SelectInsertを行います。
     *
     * @param con   DBコネクション
     * @param query クエリ
     * @param model モデル（値）のリスト
     * @param clazz modelのクラス
     */
    @Command
    public <T> int executor(Connection con, SelectInsertQuery query, T model, Class<T> clazz) throws SQLException {
        return this.db.executor(con, query, model, clazz);
    }

    /**
     * 削除します
     *
     * @param con   DBコネクション
     * @param query クエリ
     * @param model モデル（値）
     * @param clazz modelのクラス
     */
    @Command
    public <T> int executor(Connection con, DeleteQuery query, T model, Class<T> clazz) throws SQLException {
        return this.db.executor(con, query, model, clazz);
    }

    /**
     * 更新します。
     *
     * @param con   DBコネクション
     * @param query クエリ
     * @param model モデル（値）
     * @param clazz modelのクラス
     */
    @Command
    public <T> int executor(Connection con, UpdateQuery query, T model, Class<T> clazz) throws SQLException {
        if (this.conf.hasPath("system.moduleId")) {
            // そもそもClassに存在しない場合は追加してはいけない
            List<Field> fields = this.getField(clazz);
            if (fields.size() > 0) {
                // UPDATED, UPDATED_BYを、SETの条件に追加する。
                List<String> columns = Arrays.asList("UPDATED", "UPDATED_BY");
                List<String> newList = new ArrayList<>();
                List<String> list = query.getColumns();
                newList.addAll(list.stream().map(str -> str.toUpperCase()).collect(Collectors.toList()));
                columns.stream().filter(str -> !newList.contains(str)).forEach(str -> newList.add(str));
                query.setColumns(newList);
                this.set(model, fields);
            }
        }
        return this.db.executor(con, query, model, clazz);
    }

    /**
     * オブジェクトに新しい値を登録します。<br>
     * "created", "createdBy", "updated", "updatedBy"の値を置き換えます。
     *
     * @param model 対象のオブジェクト
     * @param fields
     * @throws SQLException 値の置き換えに失敗した場合
     */
    private <T> void set(T model, List<Field> fields) throws SQLException {
        Date date = DateUtils.getToday();
        String moduleId = this.conf.getString("system.moduleId");
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.getName().indexOf("By") < 0) {
                    field.set(model, date);
                } else {
                    field.set(model, moduleId);
                }
                field.setAccessible(false);
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new SQLException(e);
        }
    }

    /**
     * クラスのFieldで特定のField名のみ取得します。
     *
     * @param clazz 対象のクラス
     * @return 特定の名前のFieldのList
     */
    private List<Field> getField(Class<?> clazz) {
        List<String> columns = Arrays.asList("created", "createdBy", "updated", "updatedBy");
        Field[] fields = ReflectionUtils.getFields(clazz);
        return Arrays.asList(fields).stream().filter(f -> columns.contains(f.getName())).collect(Collectors.toList());
    }
}
