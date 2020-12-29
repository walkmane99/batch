package com.tempest.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import lombok.extern.log4j.Log4j2;

/**
 * Queryの実装です。
 */
@Log4j2
public class QueryImpl implements Query {

    private String name;
    protected Criteria criteria;
    protected String sql;
    protected String schema;
    protected Config config;
    protected Database dbMataData;

    public QueryImpl() {
        config = ConfigFactory.load();
        // DB判定
        this.dbMataData = Database.DB2;
        if (config.hasPath("db.className")) {
            this.dbMataData = Database.getDatabase(config.getString("db.className"));
        }
    }

    /**
     * テーブル名を返します。
     *
     * @return テーブル名
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * テーブル名をセットします。
     *
     * @param name テーブル名。
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * スキーマ名をセットします。
     *
     * @param schema スキーマ名
     */
    @Override
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * スキーマ名を返します。
     *
     * @return スキーマ名
     */
    @Override
    public String getSchema() {
        return this.schema;
    }

    /**
     * 条件を作成します。 条件は、シングルトンで作成され、その状態を保持します。
     *
     * @return 条件
     */
    @Override
    public Criteria createCriteria() {
        if (criteria == null) {
            this.criteria = new Criteria(this);
        }
        return this.criteria;
    }

    /**
     * @param criteria the criteria to set
     */
    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    /**
     * @return the criteria
     */
    public Criteria getCriteria() {
        return criteria;
    }

    /**
     * sqlを設定します。
     *
     * @param sql SQL
     */
    @Override
    public void setSQL(String sql) {
        this.sql = sql;
    }

    /**
     * SQLを返します。
     *
     * @return SQL
     *
     */
    @Override
    public String getSQL() {
        return this.sql;
    }

    /**
     * インスタンスの文字列形式を返します。
     *
     * SQLが設定されていればそのSQLを返します。 設定されていなければNullが返ります。
     *
     * @return SQL
     *
     */
    @Override
    public String toString() {
        return this.getSQL();
    }

    @Override
    public <T> int setCondition(PreparedStatement statements, List<T> list, int index) throws SQLException {
        int ix = index;
        for (T value : list) {
            statements.setObject(ix, value);
            ix++;
        }
        return ix;
    }
}
