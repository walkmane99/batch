package com.tempest.sql.postgresql;

import com.tempest.sql.AbstractDeleteQuery;


/**
 * 【postgresql】DELETE文を作成するクラスです。
 */
public class DeleteQuery extends AbstractDeleteQuery {


    /**
     * {@inheritDoc}
     */
    public DeleteQuery() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    public DeleteQuery(String schema, String name ) {
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
        builder.append("DELETE ");
        builder.append(" FROM ").append(this.getSchema()).append(".").append(this.getName());
        if(this.criteria != null) {
            builder.append(" WHERE ").append(this.criteria.toString());
        }
        return builder.toString();
    }

}
