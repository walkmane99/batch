package com.tempest.sql;

/**
 * DELETE文を作成するクラスです。
 */
public abstract class AbstractDeleteQuery extends QueryImpl {

    /**
     * デフォルトコンストラクタ
     * 
     */
    protected AbstractDeleteQuery() {
    }

    /**
     * @param schema スキーマ
     * @param name   テーブル名
     */
    public AbstractDeleteQuery(String schema, String name) {
        this.setSchema(schema);
        this.setName(name);
    }

    @Override
    public String toString() {
        String sql = super.toString();
        if (sql != null) {
            return sql;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("DELETE ");
        builder.append(" FROM ").append(this.getSchema()).append(".").append(this.getName());
        if (this.criteria != null) {
            builder.append(" WHERE ").append(this.criteria.toString());
        }
        return builder.toString();
    }

}