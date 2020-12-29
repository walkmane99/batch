package com.tempest.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.extern.log4j.Log4j2;

/**
 * SELECT文を作成するクラスです。
 */
@Log4j2
public abstract class AbstractSelectQuery extends QueryImpl implements Columnable {

    /**
     * カラム select句の後に続くカラム名
     */
    protected List<String> columns;
    /**
     * グループ
     *
     */
    protected List<String> groups;
    /**
     * オーダー
     */
    protected List<String> orders;

    /**
     * リミット
     */
    protected int limit;

    /**
     * オフセット
     */
    protected int offset;

    private final static int[] mutex = new int[0];

    /**
     * コンストラクタ インスタンス変数の初期化を行ってます。
     */
    protected AbstractSelectQuery() {
        this.columns = new ArrayList<>();
        this.groups = new ArrayList<>();
        this.orders = new ArrayList<>();
    }

    /**
     * コンストラクタ スキーマ名とテーブル名を設定します。
     *
     * 取得するカラムには、"*"(アスタリスク) が設定されます。
     *
     *
     * @param schema スキーマ
     * @param name   テーブル名
     */
    public AbstractSelectQuery(String schema, String name) {
        this();
        this.columns.add("* ");
        this.setSchema(schema);
        this.setName(name);
    }

    /**
     * コンストラクタ スキーマ名とテーブル名と取得するカラム名を設定します。
     *
     * @param schema  スキーマ
     * @param name    テーブル名
     * @param columns カラム名
     */
    public AbstractSelectQuery(String schema, String name, String... columns) {
        this();
        this.setSchema(schema);
        this.setName(name);
        Arrays.asList(columns).forEach(this.columns::add);
    }

    /**
     * @return the limit
     */
    public int getLimit() {
        return this.limit;
    }

    /**
     * @param limit
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * @return the limit
     */
    public int getOffset() {
        return this.offset;
    }

    /**
     * @param offset
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * オーダー（カラム名）を設定します。 カラム名の後に、スペースをつけて「ASC」または、「DESC」を設定してください。
     *
     * @param columns
     */
    public void setOrderby(String... columns) {
        Arrays.asList(columns).forEach(this.orders::add);
    }

    public List<String> getOrderby() {
        return this.orders;
    }

    /**
     * グループ（カラム名）を設定します。
     *
     * @param columns
     */
    public void setGroupby(String... columns) {
        Arrays.asList(columns).forEach(this.groups::add);
    }

    /**
     * 保持しているカラム名を返します。
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
     * SQLが設定されている場合、そのSQLの解析を行いカラムを探します。
     *
     * @param sql SQL
     */
    void analyze(String sql) {
        if (sql == null || sql.isEmpty()) {
            return;
        }
        String query = sql.substring(sql.toUpperCase().indexOf("SELECT"));
        query = query.substring(0, query.toUpperCase().indexOf("WHERE"));
        String[] cols = query.split(",");
        for (int i = 0; i < cols.length; i++) {
            String s = cols[i].trim();
            if (s.lastIndexOf(" ") > 0) {
                this.columns.add(s.substring(s.lastIndexOf(" ")).toUpperCase());
            } else {
                this.columns.add(s.toUpperCase());
            }
        }
    }
    // /**
    // * SELECT文を返します。
    // */
    // @Override
    // public String toString() {

    // //return manager.getSelectQuery(this);

    // String sql =super.toString();
    // if(sql != null) {
    // return sql;
    // }
    // StringBuilder builder = new StringBuilder();
    // builder.append("SELECT ");
    // builder.append(String.join(",", this.columns));
    // builder.append(" FROM
    // ").append(this.getSchema()).append(".").append(this.getName());

    // if(this.criteria != null) {
    // builder.append(" WHERE ").append(this.criteria.toString());
    // }
    // if(this.groups.size() > 0) {
    // builder.append(" GROUP BY ").append(String.join(",", this.groups));
    // }
    // if(this.orders.size() > 0) {
    // builder.append(" ORDER BY ").append(String.join(",", this.orders));
    // }
    // if(this.limit > 0) {
    // // limit が0以上であれば、 LIMITが設定されたとみなす。
    // builder.append(" FETCH FIRST ").append(this.limit).append(" ROWS ONLY");
    // }

    // return builder.toString();
    // }

}
