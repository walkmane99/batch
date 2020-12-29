package com.tempest.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * インサート文を作成するクラスです。
 */
public abstract class AbstractInsertQuery extends QueryImpl implements Columnable {

    protected List<String> columns;
    private final static int[] mutex = new int[0];

    /**
     * デフォルトコンストラクタ
     * 
     */
    protected AbstractInsertQuery() {
        this.columns = new ArrayList<>();

    }

    /**
     * コンストラクタ
     * 
     * @param schema  スキーマ
     * @param name    テーブル名
     * @param columns insert対象のカラム名（複数）
     */
    public AbstractInsertQuery(String schema, String name, String... columns) {
        this();
        this.setSchema(schema);
        this.setName(name);
        Arrays.asList(columns).forEach(this.columns::add);
    }

    /**
     * コンストラクタ
     * 
     * columnsで指定しきれない(XXXX_1,XXXX_2)のような場合に利用するコンストラクタ
     * columnsへの指定は、XXXX_1,XXXX_2の代わりにXXXXを指定してください。
     * 上記で指定したXXXXをcolNameに指定し（つまり、colNameで指定した値が、columnsにもあること） maxに必要な件数を指定します。
     * 
     * columnsにあるcolNameをcolName+"_"+1~maxまで作成して置き換えます。
     * 
     * @param schema  スキーマ
     * @param name    テーブル名
     * @param colName カラム名
     * @param max     必要な数
     * @param columns insert対象のカラム名（複数）
     */
    public AbstractInsertQuery(String schema, String name, String colName, int max, String... columns) {
        this();
        this.setSchema(schema);
        this.setName(name);
        this.createColumns(colName, max, columns);
    }

    /**
     * カラム名を作成します。 作成したカラム名を保持します。 columnsで指定しきれない(XXXX_1,XXXX_2)のような場合に利用するコンストラクタ
     * columnsへの指定は、XXXX_1,XXXX_2の代わりにXXXXを指定してください。
     * 上記で指定したXXXXをcolNameに指定し（つまり、colNameで指定した値が、columnsにもあること） maxに必要な件数を指定します。
     * 
     * columnsにあるcolNameをcolName+"_"+1~maxまで作成して置き換えます。
     * 
     * @param colName
     * @param max
     * @param columns
     */
    void createColumns(String colName, int max, String[] columns) {
        // 1から始まる、数字の前は＿が入る。
        String colName_ = colName + "_";
        Arrays.stream(columns).forEach(name -> {
            if (name.equals(colName)) {
                IntStream.range(0, max).forEach(x -> this.columns.add(colName_ + (x + 1)));
            } else {
                this.columns.add(name);
            }
        });
    }

    /**
     * 保持しているカラムを返します
     * 
     * @return 保持しているカラム名のリスト
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
     * sqlを解析します。
     * 
     * sqlを解析してカラム名を保持します。
     * 
     * @param sql
     */
    private void analyze(String sql) {
        if (sql == null || sql.isEmpty()) {
            return;
        }
        String query = sql.substring(sql.toUpperCase().indexOf("("), sql.toUpperCase().indexOf(")"));
        String[] cols = query.split(",");
        for (int i = 0; i < cols.length; i++) {
            String s = cols[i].trim();
            this.columns.add(s.trim().toUpperCase());
        }
    }

    /**
     * Insert文を返します。
     * 
     * @return insert文
     */
    @Override
    public String toString() {
        String sql = super.toString();
        if (sql != null) {
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