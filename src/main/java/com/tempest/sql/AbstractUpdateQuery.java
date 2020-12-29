package com.tempest.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * UPDATE文を作成します。
 * 
 */
public abstract class AbstractUpdateQuery extends QueryImpl implements Columnable {

    protected List<String> columns;

    private final static int[] mutex = new int[0];

    /**
     * コンストラクタ
     */
    public AbstractUpdateQuery() {
        this.columns = new ArrayList<>();

    }

    /**
     * コンストラクタ
     * 
     * @param schema  スキーマ名
     * @param name    テーブル名
     * @param columns 更新するカラム名
     */
    public AbstractUpdateQuery(String schema, String name, String... columns) {
        this();
        this.setSchema(schema);
        this.setName(name);
        Arrays.asList(columns).forEach(this.columns::add);
    }

    /**
     * カラム名を返します。
     * 
     * @return カラム名
     */
    @Override
    public List<String> getColumns() {
        synchronized (mutex) {
            String sql = this.getSQL();
            this.analyze(sql);
            return this.columns;
        }
    }

    @Override
    public void setColumns(List<String> list) {
        synchronized (mutex) {
            this.columns = list;
        }
    }

    /**
     * SQLを解析します。
     * 
     * @param sql SQL
     */
    private void analyze(String sql) {
        if (sql == null || sql.isEmpty()) {
            return;
        }
        String query = sql.substring(sql.toUpperCase().indexOf("SET"));
        query = query.substring(0, query.toUpperCase().indexOf("WHERE"));
        String[] cols = query.split(",");
        for (int i = 0; i < cols.length; i++) {
            String s = cols[i].trim();
            s = s.replaceAll("=", " ");
            s = s.replaceAll("?", " ");
            this.columns.add(s.trim().toUpperCase());
        }
    }

    /**
     * UPDATE文を返します。
     * 
     * @return UPDATE文
     */
    @Override
    public String toString() {
        String sql = super.toString();
        if (sql != null) {
            return sql;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE ").append(this.getSchema()).append(".").append(this.getName());
        builder.append(" SET ").append(String.join("= ?,", this.columns)).append("= ?");
        if (this.criteria != null) {
            builder.append(" WHERE ").append(this.criteria.toString());
        }
        return builder.toString();
    }

}