package com.tempest.sql;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.base.CaseFormat;
import com.tempest.ApplicationRuntimeException;
import com.tempest.annotation.Command;
import com.tempest.annotation.Component;
import com.tempest.sql.Query.Conditions;
import com.tempest.sql.Query.Symbol;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.Level;

import lombok.extern.log4j.Log4j2;

/**
 * SQLを実行するクラスです。
 *
 * Javaの型とSQLの型の変換を行います。
 *
 * 対応している型 java.util.Date timestamp java.lang.String varchar char int int short
 * SMALLINT double double float
 *
 * 配列 カラム名の最後が数字で終わる場合、以下の型に変換します。 List<String> List<Double>
 *
 * beanのプロパティ名は、DBのカラム名（スネークケース）のキャメルケース（頭小文字）でないと解決できません。
 *
 */
@Component
@SuppressWarnings("unchecked")
@Log4j2
public class SQLExecutorImpl implements SQLExecutor {

    private static SQLExecutor executor;

    public SQLExecutorImpl() {

    }

    public static SQLExecutor getInstance() {
        if (SQLExecutorImpl.executor == null) {
            SQLExecutorImpl.executor = new SQLExecutorImpl();
        }
        return SQLExecutorImpl.executor;
    }

    @Override
    @Command
    public <T> int executor(Connection con, CountQuery query, T model, Class<T> clazz) throws SQLException {
        int reslut = 0;
        ResultSet rs = null;
        PreparedStatement statements = null;
        try {
            statements = con.prepareStatement(query.toString());
            List<String> columns = query.createCriteria().getColumns();
            if (columns.size() > 0) {
                int cnt = 0;
                for (String x : columns) {
                    SQLExecutorImpl.setCondition(statements, model, x, clazz, cnt + 1);
                    cnt++;
                }
            }
            rs = statements.executeQuery();
            while (rs.next()) {
                reslut = rs.getInt(1);
            }
            return reslut;
        } catch (SQLExecutionException e) {
            throw new SQLException(e);
        } catch (SQLException e) {
            log.warn(e);
            throw e;
        } finally {
            if (statements != null) {
                try {
                    statements.close();
                } catch (SQLException e) {
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    @Override
    @Command
    public <T> List<T> executor(Connection con, SelectQuery query, T model, Class<T> clazz) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug("start select executor");
        }
        StopWatch watch = null;
        if (log.isInfoEnabled()) {
            watch = new StopWatch();
            watch.start();
        }
        List<T> list = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement statements = null;
        try {
            if (log.isDebugEnabled()) {
                log.debug("SQL: " + query.toString());
            }
            statements = con.prepareStatement(query.toString());
            List<String> columns = query.createCriteria().getColumns();
            if (columns.size() > 0) {
                int cnt = 0;

                for (String x : columns) {
                    if (log.isDebugEnabled()) {
                        log.debug("columns :" + x);
                    }
                    // そのカラムで指定した値が存在する。
                    Conditions condition = query.createCriteria().getColumn(x);
                    if (condition.getSymbol() == Symbol.IS_NULL || condition.getSymbol() == Symbol.IS_NOT_NULL) {
                        continue;
                    }
                    Object[] objects = condition.getValues();
                    if (condition.getSymbol() == Symbol.IN || condition.getSymbol() == Symbol.NOT_IN) {
                        if (objects[0] instanceof List) {
                            cnt = query.setCondition(statements, (List<?>) objects[0], cnt + 1);
                        }
                    } else {
                        if (objects != null) {
                            // 条件が入っている。
                            cnt = SQLExecutorImpl.setCondition(statements, objects, x, clazz, cnt + 1);
                        } else {
                            SQLExecutorImpl.setCondition(statements, model, x, clazz, cnt + 1);
                            cnt++;
                        }
                    }
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("SQL > " + statements.toString());
            }
            rs = statements.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            while (rs.next()) {
                T t = create(rs, metaData, clazz);
                if (t != null) {
                    list.add(t);
                }
            }
            if (log.isInfoEnabled()) {
                watch.stop();
                if (list.size() > 0) {
                    log.info("SQL:" + query.toString());
                    log.info("size:" + list.size() + "time:" + watch.getTime() + " ms");
                }
            }
            return list;
        } catch (SQLExecutionException e) {
            throw new SQLException(e);
        } catch (SQLException e) {
            log.warn(e);
            throw e;
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("end select executor");
            }
            if (statements != null) {
                try {
                    statements.close();
                } catch (SQLException e) {
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    @Override
    @Command
    public <T> int executor(Connection con, InsertQuery query, List<T> models, Class<T> clazz) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug("start insert executor :" + query.toString());
        }
        PreparedStatement statements = null;
        try {
            statements = con.prepareStatement(query.toString());
            for (T model : models) {
                List<String> columns = query.getColumns();
                if (columns.size() > 0) {
                    int cnt = 0;
                    for (String x : columns) {
                        SQLExecutorImpl.setCondition(statements, model, x, clazz, cnt + 1);
                        cnt++;
                    }
                }
                statements.addBatch();
                // statements.executeUpdate();
            }
            statements.executeBatch();
            return models.size();
        } catch (SQLExecutionException e) {
            throw new SQLException(e);
        } finally {
            if (statements != null) {
                try {
                    statements.close();
                } catch (SQLException e) {
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("start insert executor");
            }
        }
    }

    @Override
    @Command
    public <T> int executor(Connection con, InsertQuery query, T model, Class<T> clazz) throws SQLException {
        PreparedStatement statements = null;
        try {
            statements = con.prepareStatement(query.toString());
            List<String> columns = query.getColumns();
            if (columns.size() > 0) {
                int cnt = 0;
                for (String x : columns) {
                    SQLExecutorImpl.setCondition(statements, model, x, clazz, cnt + 1);
                    cnt++;
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("SQL > " + statements.toString());
            }

            return statements.executeUpdate();
        } catch (SQLExecutionException e) {
            throw new SQLException(e);
        } finally {
            if (statements != null) {
                try {
                    statements.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    /**
     * 挿入します。
     *
     * @param con   DBコネクション
     * @param query クエリ
     * @param model モデル（値）のリスト
     * @param clazz modelのクラス
     */
    @Override
    @Command
    public <T> int executor(Connection con, SelectInsertQuery query, T model, Class<T> clazz) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug("start selectInsert executor");
        }
        StopWatch watch = null;
        if (log.isInfoEnabled()) {
            watch = new StopWatch();
            watch.start();
        }
        try (PreparedStatement statements = con.prepareStatement(query.toString())) {
            if (log.isDebugEnabled()) {
                log.debug("SQL: " + query.toString());
            }
            List<String> columns = query.createCriteria().getColumns();
            if (columns.size() > 0) {
                int cnt = 0;
                for (String x : columns) {
                    if (log.isDebugEnabled()) {
                        log.debug("columns :" + x);
                    }
                    // そのカラムで指定した値が存在する。
                    Conditions condition = query.criteria.getColumn(x);
                    Object[] objects = condition.getValues();
                    if (objects != null) {
                        // 条件が入っている。
                        cnt = SQLExecutorImpl.setCondition(statements, objects, x, clazz, cnt + 1);
                    } else {
                        SQLExecutorImpl.setCondition(statements, model, x, clazz, cnt + 1);
                        cnt++;
                    }
                }
            }
            if (log.isInfoEnabled()) {
                watch.stop();
                log.info(watch.getTime());
            }
            return statements.executeUpdate();
        } catch (SQLExecutionException e) {
            throw new SQLException(e);
        } catch (SQLException e) {
            log.warn(e);
            throw e;
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("end select executor");
            }
        }
    }

    @Override
    @Command
    public <T> int executor(Connection con, DeleteQuery query, T model, Class<T> clazz) throws SQLException {
        PreparedStatement statements = null;
        try {
            statements = con.prepareStatement(query.toString());
            List<String> columns = query.createCriteria().getColumns();
            if (columns.size() > 0) {
                int cnt = 0;
                for (String x : columns) {
                    SQLExecutorImpl.setCondition(statements, model, x, clazz, cnt + 1);
                    cnt++;
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("SQL > " + statements.toString());
            }

            return statements.executeUpdate();
        } catch (SQLExecutionException e) {
            throw new SQLException(e);
        } finally {
            if (statements != null) {
                try {
                    statements.close();
                } catch (SQLException e) {
                }
            }
        }

    }

    @Command
    public <T> int executor(Connection con, UpdateQuery query, T model, Class<T> clazz) throws SQLException {
        PreparedStatement statements = null;
        if (log.isDebugEnabled()) {
            log.debug("executor(update) start");
            log.debug("SQL: " + query.toString());
        }
        try {
            statements = con.prepareStatement(query.toString());
            List<String> columns = query.getColumns();
            if (columns.size() == 0) {
                throw new ApplicationRuntimeException(new SQLExecutionException("not defined a column to change."));
            }
            int cnt = 0;
            for (String x : columns) {
                SQLExecutorImpl.setCondition(statements, model, x, clazz, cnt + 1);
                cnt++;
            }

            List<String> criteria = query.createCriteria().getColumns();
            if (criteria.size() > 0) {
                for (String x : criteria) {
                    if (log.isDebugEnabled()) {
                        log.debug("criteria :" + x);
                    }

                    // そのカラムで指定した値が存在する。
                    Conditions condition = query.createCriteria().getColumn(x);
                    if (condition.getSymbol() == Symbol.IS_NULL || condition.getSymbol() == Symbol.IS_NOT_NULL) {
                        continue;
                    }
                    Object[] objects = condition.getValues();
                    if (condition.getSymbol() == Symbol.IN || condition.getSymbol() == Symbol.NOT_IN) {
                        if (objects[0] instanceof List) {
                            cnt = query.setCondition(statements, (List<?>) objects[0], cnt + 1);
                        }
                    } else {
                        if (objects != null) {
                            // 条件が入っている。
                            cnt = SQLExecutorImpl.setCondition(statements, objects, x, clazz, cnt + 1);
                        } else {
                            SQLExecutorImpl.setCondition(statements, model, x, clazz, cnt + 1);
                            cnt++;
                        }
                    }
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("SQL > " + statements.toString());
            }
            return statements.executeUpdate();
        } catch (SQLExecutionException e) {
            throw new SQLException(e);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("executor(update) end");
            }
            if (statements != null) {
                try {
                    statements.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    static <T> T create(ResultSet rs, ResultSetMetaData metaData, Class<T> clazz)
            throws SQLException, SQLExecutionException {
        try {
            boolean filled = false;
            T his = (T) clazz.getDeclaredConstructor().newInstance();
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                String colName = metaData.getColumnName(i + 1).trim();
                PropertyDescriptor property = SQLExecutorImpl.getPropertyDescriptor(clazz, colName);
                Method writeMethod = property.getWriteMethod();
                Method readMethod = property.getReadMethod();
                boolean result = SQLExecutorImpl.setResult(rs, his, writeMethod, readMethod, colName);
                filled = filled || result;
            }
            return filled ? his : null;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new ApplicationRuntimeException(new SQLExecutionException(e));
        }
    }

    static <T> boolean setResult(ResultSet rs, T model, Method writeMethod, Method readMethod, String colName)
            throws SQLException {
        log.trace("start setResult");
        Class<?> clazz = writeMethod.getParameterTypes()[0];
        log.trace(colName);
        boolean filled = false;
        try {
            // カラム名の後ろが数字のものは、連続した配列に格納するものとみなす。またはList
            // あるいはモジュールに、配列またはlistがある場合。
            if (clazz == List.class) {
                Type[] types = writeMethod.getGenericParameterTypes();
                // Now assuming that the first parameter to the method is of type List<Integer>
                ParameterizedType pType = (ParameterizedType) types[0];
                Class<?> genericClass = (Class<?>) pType.getActualTypeArguments()[0];
                if (genericClass == String.class) {
                    String result = rs.getString(colName);
                    List<String> list = (List<String>) readMethod.invoke(model, new Object[] {});
                    if (!rs.wasNull()) {
                        list.add(result.trim());
                        filled = true;
                    } else {
                        list.add(null);
                    }
                } else if (genericClass == Double.class) {
                    BigDecimal result = rs.getBigDecimal(colName);
                    List<Double> list = (List<Double>) readMethod.invoke(model, new Object[] {});
                    if (!rs.wasNull()) {
                        list.add(result.doubleValue());
                        filled = true;
                    } else {
                        list.add(null);
                    }
                }
            } else if (clazz == String.class) {
                String result = rs.getString(colName);
                if (!rs.wasNull()) {
                    if (log.isTraceEnabled()) {
                        log.trace(colName + " : " + result);
                    }
                    writeMethod.invoke(model, new Object[] { result.trim() });
                    filled = true;
                }
            } else if (clazz == Date.class) {
                Timestamp stamp = rs.getTimestamp(colName);
                if (!rs.wasNull()) {
                    Date result = new Date(stamp.getTime());
                    writeMethod.invoke(model, new Object[] { result });
                    filled = true;
                }
            } else if (clazz == Integer.class || clazz == int.class || clazz == Short.class || clazz == short.class) {
                int result = rs.getInt(colName);
                if (!rs.wasNull()) {
                    if (clazz == Short.class || clazz == short.class) {
                        writeMethod.invoke(model, new Object[] { (short) result });
                    } else {
                        writeMethod.invoke(model, new Object[] { result });
                    }
                    filled = true;
                }
            } else if (clazz == BigDecimal.class || clazz == Float.class || clazz == float.class
                    || clazz == Double.class || clazz == double.class) {
                BigDecimal result = rs.getBigDecimal(colName);
                if (!rs.wasNull()) {
                    if (clazz == Double.class || clazz == double.class) {
                        writeMethod.invoke(model, new Object[] { result.doubleValue() });
                    } else if (clazz == Float.class || clazz == float.class) {
                        writeMethod.invoke(model, new Object[] { result.floatValue() });
                    } else {
                        writeMethod.invoke(model, new Object[] { result });
                    }
                    filled = true;
                }
            }
            return filled;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
            throw new ApplicationRuntimeException(new SQLExecutionException(e));
        }

    }

    static <T> int setCondition(PreparedStatement statements, Object[] values, String columName, Class<T> ca, int index)
            throws SQLException, SQLExecutionException {
        // Field field = ca.getDeclaredField(columName);
        PropertyDescriptor property = SQLExecutorImpl.getPropertyDescriptor(ca, columName);
        Method method = property.getWriteMethod();
        try {
            int ix = index;
            T his = (T) ca.getDeclaredConstructor().newInstance();
            for (int i = 0; i < 2; i++) {
                Object obj = values[i];
                method.invoke(his, new Object[] { obj });
                ix += i;
                setCondition(statements, his, columName, ca, ix);
            }
            return ix;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new SQLException(e);
        }
    }

    /**
     * 条件にパラメータをセットします。
     *
     * @throws SQLExecutionException
     *
     */
    static <T> void setCondition(PreparedStatement statements, T model, String columName, Class<?> ca, int index)
            throws SQLException, SQLExecutionException {
        if (log.isDebugEnabled()) {
            log.debug("start setCondition");
        }
        PropertyDescriptor property = SQLExecutorImpl.getPropertyDescriptor(ca, columName);
        Method method = property.getReadMethod();
        Class<?> clazz = method.getReturnType();
        try {
            Object obj = method.invoke(model, new Object[] {});
            if (log.isDebugEnabled()) {
                log.debug("value : " + obj);
            }
            if (clazz == List.class) {
                // ここでカラム名にある添え字(inptVal_1の1)を使って値を設定している。
                Type[] types = property.getWriteMethod().getGenericParameterTypes();
                // Now assuming that the first parameter to the method is of type List<Integer>
                ParameterizedType pType = (ParameterizedType) types[0];
                Class<?> genericClass = (Class<?>) pType.getActualTypeArguments()[0];
                int i = Integer.parseInt(columName.substring(columName.lastIndexOf("_") + 1)) - 1;
                if (genericClass == String.class) {
                    if (obj == null) {
                        // ListがNull
                        statements.setNull(index, Types.VARCHAR);
                    } else {
                        List<String> list = (List<String>) obj;
                        if (list.size() > i) {
                            // Listのサイズ内に指定の添え字が存在する。
                            String value = list.get(i);
                            if (value == null) {
                                // 取得した値がNULL
                                statements.setNull(index, Types.VARCHAR);
                            } else {
                                statements.setString(index, value);
                            }
                        } else {
                            // 保持しているListのサイズを超えた場合。
                            statements.setNull(index, Types.VARCHAR);
                        }
                    }
                } else if (genericClass == Double.class) {
                    if (obj == null) {
                        // ListがNull
                        statements.setNull(index, Types.FLOAT);
                    } else {
                        List<Double> list = (List<Double>) obj;
                        if (list.size() > i) {
                            // Listのサイズ内に指定の添え字が存在する。
                            Double value = list.get(i);
                            if (value == null) {
                                // 取得した値がNULL
                                statements.setNull(index, Types.FLOAT);
                            } else {
                                statements.setDouble(index, value);
                            }
                        } else {
                            // 保持しているListのサイズを超えた場合。
                            statements.setNull(index, Types.FLOAT);
                        }
                    }
                }
            } else if (clazz == String.class || clazz == Character.class || clazz == char.class) {
                if (log.isDebugEnabled()) {
                    log.debug("string ");
                    log.debug("index: " + index);
                }
                if (obj == null) {
                    statements.setNull(index, Types.VARCHAR);
                } else {
                    statements.setString(index, String.valueOf(obj));
                }
            } else if (clazz == Date.class) {
                if (obj == null) {
                    statements.setNull(index, Types.TIMESTAMP);
                } else {
                    statements.setTimestamp(index, new Timestamp(((Date) obj).getTime()));
                }
            } else if (clazz == Integer.class || clazz == int.class || clazz == Short.class || clazz == short.class) {
                if (log.isDebugEnabled()) {
                    log.debug("int ");
                    log.debug("index: " + index);
                }
                if (obj == null) {
                    statements.setNull(index, Types.INTEGER);
                } else {
                    String s = String.valueOf(obj);
                    statements.setInt(index, Integer.valueOf(s));
                }
            } else if (clazz == BigDecimal.class || clazz == Float.class || clazz == float.class
                    || clazz == Double.class || clazz == double.class) {
                if (obj == null) {
                    statements.setNull(index, Types.FLOAT);
                } else {
                    String s = String.valueOf(obj);
                    BigDecimal decimal = new BigDecimal(s);
                    statements.setFloat(index, decimal.floatValue());
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("other ");
                    log.debug("class: " + clazz.getName());
                }
                throw new RuntimeException("サポート対象の型でない値が設定された。");
            }
        } catch (IllegalAccessException | InvocationTargetException | RuntimeException e) {
            throw new ApplicationRuntimeException(new SQLExecutionException(e));
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("end setCondition");
            }
        }
    }

    /**
     * PropertyDescriptorを返します。
     * カラム名からプロパティ名を作成し、clazzからフィールドプロパティを特定してPropertyDescriptorとして返します。
     *
     * クラスのプロパティ名は、カラム名（スネークケース）をキャメルケース（先頭小文字）で指定していることが前提です。
     * カラム名の末尾が数字で終わるものは、最後の_(アンダーバー)で切り分けた前をプロパティ名としています。
     *
     * @param clazz      クラス
     * @param columnName カラム名
     *
     * @return PropertyDescriptor
     * @throws SQLExecutionException
     */
    static <T> PropertyDescriptor getPropertyDescriptor(Class<T> clazz, String columnName)
            throws SQLExecutionException {
        if (log.isTraceEnabled()) {
            log.trace("start getPropertyDescriptor");
            log.trace("class : " + clazz.getName());

        }
        String name = columnName;
        if (log.isTraceEnabled()) {
            log.trace("name: " + name);
        }
        if (name.matches(".*\\d+$")) {
            // 末尾が数字のカラムは、末尾の数字を切り捨てる。
            name = name.substring(0, name.lastIndexOf("_"));
        }
        name = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name);
        if (log.isTraceEnabled()) {
            log.trace("UPPER_UNDERSCORE name: " + name);
        }
        try {
            return new PropertyDescriptor(name, clazz);
        } catch (IntrospectionException e) {
            log.catching(Level.TRACE, e);
            throw new SQLExecutionException(e);
        } finally {
            if (log.isTraceEnabled()) {
                log.trace("end getPropertyDescriptor");
            }
        }
    }
}
