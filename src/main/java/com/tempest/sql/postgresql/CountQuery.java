package com.tempest.sql.postgresql;

import com.tempest.sql.AbstractSelectQuery;

/**
 * 【postgresql】SELECT文を作成するクラスです。
 * Count文に特化したSQLを作成します。
 */
public class CountQuery extends AbstractSelectQuery {
    /**
     *  コンストラクタ
     *  スキーマ名とテーブル名を設定します。
     *
     *
     * @param schema　スキーマ
     * @param name テーブル名
     */
    public CountQuery(String schema, String name ) {
        super(schema, name);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        String sql =super.toString();
        if(sql != null) {
            return sql;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT COUNT(*) AS CNT");
        builder.append(" FROM ").append(this.getSchema()).append(".").append(this.getName());

        if(this.criteria != null) {
            builder.append(" WHERE ").append(this.criteria.toString());
        }
        if(this.groups.size() > 0) {
            builder.append(" GROUP BY ").append(String.join(",", this.groups));

        }

        return builder.toString();
    }


}
