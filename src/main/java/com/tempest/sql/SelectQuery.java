package com.tempest.sql;



import com.tempest.sql.AbstractSelectQuery;
/**
 * 【DB2】SELECT文を作成するクラスです。
 */
public class SelectQuery extends AbstractSelectQuery {


    /**
     * コンストラクタ
     * インスタンス変数の初期化を行ってます。
     */
    public SelectQuery( ) {
        super();

    }
    /**
     *  コンストラクタ
     *  スキーマ名とテーブル名を設定します。
     *
     *  取得するカラムには、"*"(アスタリスク)　が設定されます。
     * 　
     *
     * @param schema　スキーマ
     * @param name テーブル名
     */
    public SelectQuery(String schema, String name ) {
        super(schema, name);
    }
    /**
     *  コンストラクタ
     *  スキーマ名とテーブル名と取得するカラム名を設定します。
     *
     * @param schema　スキーマ
     * @param name テーブル名
     * @param columns カラム名
     */
    public SelectQuery(String schema, String name, String ... columns ) {
        super(schema, name, columns);
    }



    /**
     * SELECT文を返します。
     */
    @Override
    public String toString() {
        //return manager.getSelectQuery(this);

        String sql =super.toString();
        if(sql != null) {
            return sql;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        builder.append(String.join(",", this.columns));
        builder.append(" FROM ").append(this.getSchema()).append(".").append(this.getName());

        if(this.criteria != null) {
            builder.append(" WHERE ").append(this.criteria.toString());
        }
        if(this.groups.size() > 0) {
            builder.append(" GROUP BY ").append(String.join(",", this.groups));
        }
        if(this.orders.size() > 0) {
            builder.append(" ORDER BY ").append(String.join(",", this.orders));
        }
        if(this.limit > 0) {
            // limit が0以上であれば、　LIMITが設定されたとみなす。
            builder.append(" FETCH FIRST ").append(this.limit).append(" ROWS ONLY");
        }
        return builder.toString();
    }


}
