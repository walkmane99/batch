package com.tempest.sql;



import com.tempest.sql.AbstractUpdateQuery;
/**
 * 【DB2】UPDATE文を作成します。
 *
 */
public class UpdateQuery extends AbstractUpdateQuery {

    /**
     * コンストラクタ
     */
    public UpdateQuery( ) {
        super();
    }
    /**
     * コンストラクタ
     * @param schema スキーマ名
     * @param name　テーブル名
     * @param columns　更新するカラム名
     */
    public UpdateQuery(String schema,String name, String ... columns ) {
        super(schema, name, columns);
    }
    /**
     * UPDATE文を返します。
     * @return UPDATE文
     */
    @Override
    public String toString() {
        String sql =super.toString();
        if(sql != null) {
            return sql;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE ").append(this.getSchema()).append(".").append(this.getName());
        builder.append(" SET ").append(String.join("= ?,", this.columns)).append("= ?");
        if(this.criteria != null) {
            builder.append(" WHERE ").append(this.criteria.toString());
        }
        return builder.toString();
    }

}
