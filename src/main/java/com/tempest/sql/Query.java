package com.tempest.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.log4j.Log4j2;

/**
 * 関数で利用したい。
 *
 * 今まで　
 * 　DBクエリ発行　＞　ORMでDAOに取り込みListにする　＞　コネクションを返す、　> アプリでListを利用
 *
 * 目指すばしょ
 * 　クエリ発行　＞　戻り値毎に処理する関数を動かす（アプリで利用）　＞　コネクションを返す。
 *
 *
 * Qurty q = new Query(<<SQL>>);
 * q.execute();  // update insert
 * q.execute(DAO.class);
 * //selectの時だけ
 * q.forEach(r->{});
 * q.map(r->{}).toList();
 *
 */
public interface Query {
    /**
     * テーブル名を返します。
     *
     * @return テーブル名
     */
    String getName();

    /**
     * テーブル名を設定します。
     *
     * @param name テーブル名
     */
    void setName(String name);

    /**
     * スキーマ名を設定します。
     *
     * @param schema スキーマ名
     */
    void setSchema(String schema);

    /**
     * スキーマ名を返します。
     *
     * @return スキーマ名
     */
    String getSchema();

    /**
     * SQLを設定します。 ここで指定したSQLが必ず利用されます。
     *
     * @param sql SQL
     */
    void setSQL(String sql);

    /**
     * SQLを返します
     *
     * @return SQL
     */
    String getSQL();

    /**
     * 条件を表すインスタンス作成し返します。
     *
     * @return 条件を表すインスタンス
     */
    Criteria createCriteria();

    <T> int setCondition(PreparedStatement statements, List<T> obj, int index) throws SQLException;

    /**
     * 条件を表すクラスです。
     *
     * and,orの単調な条件しか表せません。
     *
     */
    @Log4j2
    public class Criteria {
        /**
         * カラム名と、条件を保持するMap
         */
        private List<Conditions> list;
        /**
         * 親クエリ
         */
        private Query query;

        /**
         * コンストラクタ
         *
         * @param query 親
         */
        public Criteria(Query query) {
            this.list = new ArrayList<>();
            this.query = query;
        }

        /**
         * 条件を追加します。 値との比較は等価比較となります。
         *
         * @param column     カラム名
         * @param expression 次の条件との関係性（AND,OR)
         */
        public void add(String column, Expression expression) {
            this.add(column, expression, Symbol.EQUAL);
        }

        /**
         * 条件を追加します。
         *
         * @param column     カラム名
         * @param expression 次の条件との関係性（AND,OR)
         * @param symbol     値との比較子
         */
        public void add(String column, Expression expression, String symbol) {
            this.list.add(new Conditions(column, expression, symbol));
        }

        /**
         * 条件を追加します。
         *
         * @param column     カラム名
         * @param expression 次の条件との関係性（AND,OR)
         * @param symbol     値との比較子
         */
        public void add(String column, Expression expression, Symbol symbol) {
            this.list.add(new Conditions(column, expression, symbol));
        }

        /**
         * 条件を追加します。
         *
         * @param column     カラム名
         * @param expression 次の条件との関係性（AND,OR)
         * @param values     検索条件
         */
        public void add(String column, Expression expression, Object[] values) {
            this.list.add(new Conditions(column, expression, values));
        }

        /**
         * 条件を追加します。
         *
         * @param column     カラム名
         * @param expression 次の条件との関係性（AND,OR)
         * @param values     検索条件
         */
        public void add(String column, Expression expression, Symbol symbol, Object[] values) {
            this.list.add(new Conditions(column, expression, symbol, values));
        }

        /**
         * 条件に登録されているカラム名を取得します。
         */
        public List<String> getColumns() {
            String sql = this.query.getSQL();
            this.analyze(sql);
            return this.list.stream().map(condition -> condition.getColumnName()).collect(Collectors.toList());
        }

        /**
         * 条件の中から引数に該当する条件を返します。
         *
         * @return 条件
         */
        public List<Conditions> getConditions() {
            return this.list;
        }

        /**
         * 条件の中から引数に該当する条件を返します。
         *
         * @param columnName カラム名
         * @return 条件
         */
        public Conditions getColumn(String columnName) {
            return this.list.stream().filter(condition -> condition.getColumnName().equals(columnName)).findFirst()
                    .orElse(null);
        }

        /**
         * SQLを解析します。 直接SQLを指定している場合、その条件に書かれたカラム名を収集します。
         *
         *
         */
        private void analyze(String sql) {
            if (sql == null || sql.isEmpty()) {
                return;
            }
            if (sql.toUpperCase().indexOf("WHERE") == -1) {
                return;
            }
            String query = sql.substring(sql.toUpperCase().indexOf("WHERE"));
            if (query.toUpperCase().indexOf("GROUP") > 0) {
                query = query.substring(0, query.toUpperCase().indexOf("GROUP"));
            }

            if (query.toUpperCase().indexOf("ORDER") > 0) {
                query = query.substring(0, query.toUpperCase().indexOf("ORDER"));
            }
            // クエリ内の比較子を排除
            query = query.replaceAll("=", " ");
            query = query.replaceAll(">", " ");
            query = query.replaceAll("<", " ");
            query = query.replaceAll("!", " ");
            query = query.replaceAll("\\)", " ");
            query = query.toUpperCase().replaceAll("IS ", " ");
            query = query.toUpperCase().replaceAll("LIKE", " ");
            query = query.toUpperCase().replaceAll("BETWEEN", " "); // これがあるとつらい。AAA ? AND ? みたいになってるはず
            query = query.replaceAll("NOT", " ");
            // この状態だと？の前が必要なカラム名のはず。
            String[] cols = query.split(" ");
            cols = Arrays.stream(cols).filter(s -> !s.trim().isEmpty()).toArray(String[]::new);
            for (int i = 0; i < cols.length; i++) {
                String s = cols[i];
                if (s.trim().equals("?")) {
                    if (i > 0) {
                        String name = cols[i - 1].trim();
                        if (!name.toUpperCase().equals("AND")) {
                            this.list.add(new Conditions(name, Expression.and, "="));
                        }
                    }
                }
            }
        }

        /**
         * 条件を文字列にして返します。
         *
         * @return 条件文
         */
        @Override
        public String toString() {
            if (this.list.size() == 0) {
                return null;
            }
            boolean bool = false;
            StringBuilder builder = new StringBuilder();
            for (Conditions conditions : this.list) {
                if (bool) {
                    builder.append(conditions.getExp().toString());
                }
                builder.append(conditions.getColumnName()).append(" ")
                        .append(conditions.getSymbol().getConditionString(conditions.getColumnName(), this));
                bool = true;
            }
            return builder.toString();
        }
    }

    /**
     * 個々の条件を表すクラスです。
     */
    public class Conditions {
        private String columnName;
        private Expression exp;
        private Symbol symbol;
        private Object[] values;

        /**
         * コンストラクタ
         *
         * @param exp    次の条件との関係
         * @param symbol 比較子
         */
        Conditions(String columnName, Expression exp, String symbol) {
            this.setColumnName(columnName);
            this.exp = exp;
            this.symbol = Symbol.parseSymbol(symbol);
        }

        /**
         * コンストラクタ
         *
         * @param exp    次の条件との関係
         * @param symbol 比較子
         */
        Conditions(String columnName, Expression exp, Symbol symbol) {
            this.setColumnName(columnName);
            this.exp = exp;
            this.symbol = symbol;
        }

        Conditions(String columnName, Expression exp, Symbol symbol, Object[] value) {
            this(columnName, exp, symbol);
            this.values = value;
        }

        Conditions(String columnName, Expression exp, Object[] value) {
            this(columnName, exp, Symbol.BETWEEN, value);
        }

        /**
         * カラム名を返します。
         *
         * @return カラム名
         */
        public String getColumnName() {
            return this.columnName;
        }

        /**
         * カラム名を設定します。
         *
         * @param columnName カラム名
         */
        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        /**
         * カラムに定義した値を返します。
         */
        public Object[] getValues() {
            return this.values;
        }

        /**
         * 次の条件との関係を返す。
         *
         * @return the exp
         */
        public Expression getExp() {
            return exp;
        }

        /**
         * 比較子を返す。
         *
         * @return the symbol
         */
        public Symbol getSymbol() {
            return symbol;
        }
    }

    /**
     * 関係性を表す
     */
    public static enum Expression {
        and(" AND "), or(" OR ");

        private String str;

        Expression(String str) {
            this.str = str;
        }

        @Override
        public String toString() {
            return this.str;
        }
    }

    public static enum Symbol {
        EQUAL("=", null), IN("IN", null), NOT_IN("NOT IN", null), IS_NULL("IS NULL", null),
        IS_NOT_NULL("IS NOT NULL", null), BETWEEN("BETWEEN", null), LIKE("LIKE ", null),
        /** TARGET_COLUME <= ? */
        FOLLOWING("<=", null),
        /** TARGET_COLUME < ? */
        UNDER("<", null),
        /** TARGET_COLUME &gt;= ? */
        ABOVE(">=", null),
        /** TARGET_COLUME &gt; ? */
        OVER(">", null),
        /** TARGET_COLUME &gt; ? AND TARGET_COLUME < ? */
        OVER_UNDER(">", "<"),
        /** TARGET_COLUME &gt;= ? AND TARGET_COLUME <= ? */
        ABOVE_FOLLOWING(">=", "<="),
        /** TARGET_COLUME &gt;= ? AND TARGET_COLUME < ? */
        ABOVE_UNDER(">=", "<"),
        /** TARGET_COLUME &gt; ? AND TARGET_COLUME <= ? */
        OVER_FOLLOWING(">", "<=");

        private String from;
        private String to;

        Symbol(String from, String to) {
            this.from = from;
            this.to = to;
        }

        public String getFrom() {
            return this.from;
        }

        public String getTo() {
            return this.to;
        }

        public static Symbol parseSymbol(String code) {
            return Arrays.stream(Symbol.values()).filter(x -> x.getFrom().equals(code)).findFirst().orElse(EQUAL);
        }

        public String getConditionString(String column, Criteria criteria) {
            StringBuilder builder = new StringBuilder();
            switch (this) {
            case EQUAL:
            case FOLLOWING:
            case UNDER:
            case ABOVE:
            case OVER:
                builder.append(this.from).append(" ? ");
                break;
            case LIKE:
                builder.append(this.from).append(" ? ");
                break;
            case IN:
            case NOT_IN:
                // criteria.query.getIn
                List<?> list = (List<?>) criteria.getColumn(column).getValues()[0];
                builder.append(this.from).append(" (").append(String.join(",", Collections.nCopies(list.size(), "?")))
                        .append(")");
                break;
            case BETWEEN:
                builder.append(this.from).append(" ? AND ? ");
                break;
            case ABOVE_UNDER:
            case OVER_UNDER:
            case OVER_FOLLOWING:
            case ABOVE_FOLLOWING:
                builder.append(this.from).append(" ? AND ").append(column).append(" ").append(this.to).append(" ? ");
                break;
            case IS_NOT_NULL:
            case IS_NULL:
                builder.append(this.from);
                break;

            }
            return builder.toString();
        }
    }
}
