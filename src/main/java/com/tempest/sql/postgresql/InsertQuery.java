package com.tempest.sql.postgresql;

import java.util.Arrays;

import com.tempest.sql.AbstractInsertQuery;

/**
 * 【postgresql】インサート文を作成するクラスです。
 */
public class InsertQuery extends AbstractInsertQuery {

    /**
     * デフォルトコンストラクタ
     *
     */
    public InsertQuery( ) {
       super();
    }

    /**
     * コンストラクタ
     *
     * @param schema スキーマ
     * @param name テーブル名
     * @param columns insert対象のカラム名（複数）
     */
    public InsertQuery(String schema, String name, String ... columns ) {
        super(schema, name, columns);
    }

    /**
     * コンストラクタ
     *
     * columnsで指定しきれない(XXXX_1,XXXX_2)のような場合に利用するコンストラクタ
     * columnsへの指定は、XXXX_1,XXXX_2の代わりにXXXXを指定してください。
     * 上記で指定したXXXXをcolNameに指定し（つまり、colNameで指定した値が、columnsにもあること）
     * maxに必要な件数を指定します。
     *
     * columnsにあるcolNameをcolName+"_"+1~maxまで作成して置き換えます。
     *
     * @param schema スキーマ
     * @param name テーブル名
     * @param colName カラム名
     * @param max 必要な数
     * @param columns insert対象のカラム名（複数）
     */
    public InsertQuery(String schema, String name, String colName, int max, String ... columns ) {
        super(schema, name, colName, max, columns);
    }


    /**
     * Insert文を返します。
     *
     * @return insert文
     */
    @Override
    public String toString() {
        String sql =super.toString();
        if(sql != null) {
            return sql;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO ").append(this.getSchema()).append(".").append(this.getName());
        builder.append("(").append(String.join(",", this.columns)).append(")");
        String[] question = new String[this.columns.size()];
        Arrays.fill(question, "?");
        builder.append(" VALUES (").append(String.join(",", question)).append(")");
        return builder.toString();
    }

}
