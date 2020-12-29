package com.tempest.sql.postgresql;


import com.tempest.sql.AbstractSelectInsertQuery;

/**
 * 【postgresql】インサート文を作成するクラスです。
 */
public class SelectInsertQuery extends AbstractSelectInsertQuery {

    /**
     * {@inheritDoc}
     */
    public SelectInsertQuery( ) {
       super();
    }

    /**
     * {@inheritDoc}
     */
    public SelectInsertQuery(String fromSchema, String fromName, String toSchema, String toName, String ... columns ) {
        super(fromSchema, fromName, toSchema, toName, columns);
    }

    /**
     * {@inheritDoc}
     */
    public SelectInsertQuery(String fromSchema, String fromName, String toSchema, String toName, String colName, int max, String ... columns ) {
        super(fromSchema, fromName, toSchema, toName, colName, max, columns);

    }



}
