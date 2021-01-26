package com.tempest.db;

import com.google.common.base.CaseFormat;
import com.tempest.ApplicationRuntimeException;
import com.tempest.sql.SQLExecutionException;
import com.tempest.sql.system.ConnectionPool;
import com.tempest.utils.FaildCreateObjectException;
import com.tempest.utils.ReflectionUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tempest.function.LambdaExceptionUtil.*;

@Log4j2
public final class Result<T> {

    private Query query;

    private Class<T> targetClass;

    Result() {
    }

    Result(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    void setQuery(Query query) {
        this.query = query;
    }

    int execute(Connection con, Consumer<T> consumer) throws SQLException, FaildCreateObjectException {
        SQLAnalyzer analyzer = new SQLAnalyzer();
        analyzer.analyze(this.query.getSQL());
        PreparedStatement statement = con.prepareStatement(analyzer.getSQL());
        // TODO;in区とかListを渡したいときの対応
        Map<Integer, Condition<?>> conditionMap = analyzer.getCondition(this.query.getConditions());
        conditionMap.entrySet().forEach(rethrowConsumer(entry -> entry.getValue().set(entry.getKey(), statement)));
        // 実行するSQLのタイプに寄って処理を分岐する。
        if (analyzer.isSelect()) {
            this.executeQuery(statement, consumer);
            // TODO:件数
            return 0;
        } else {
            return this.executeUpdate(statement);
        }
    }

    int execute(Connection con, List<T> list) throws SQLException, FaildCreateObjectException, IllegalAccessException {
        SQLAnalyzer analyzer = new SQLAnalyzer();
        analyzer.analyze(this.query.getSQL());
        PreparedStatement statement = con.prepareStatement(analyzer.getSQL());

        Class<T> clazz = (Class<T>) list.get(0).getClass();
        Field[] fields = ReflectionUtils.getFields(clazz);
        // Map<Integer, Condition<?>> conditionMap =
        list.stream().flatMap(rethrowFunction(record -> {
            List<Condition<?>> conditions = Stream.of(fields).map(rethrowFunction(field -> {
                Condition<?> result = null;
                if (field.getDeclaringClass() == String.class) {
                    result = new StringCondition(field.getName(), (String) field.get(record));
                }
                result = new StringCondition("1", "");
                return result;
            })).collect(Collectors.toList());
            return analyzer.getCondition(conditions).entrySet().stream();

        })).forEach(rethrowConsumer(entry -> entry.getValue().set(entry.getKey(), statement)));

        if (!analyzer.isSelect()) {
            return this.executeUpdate(statement);
        }
        return 0;
    }

    /**
     * 更新処理系
     * 
     * @param statement
     * @return
     * @throws SQLException
     */
    private int executeUpdate(PreparedStatement statement) throws SQLException {
        return statement.executeUpdate();
    }

    /**
     * 検索刑
     * 
     * @param statement
     * @param consumer
     * @return
     * @throws SQLException
     */
    private void executeQuery(PreparedStatement statement, Consumer<T> consumer)
            throws SQLException, FaildCreateObjectException {
        try (ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                T row = this.getRow(rs);
                consumer.accept(row);
            }
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
            }
        }
    }

    private T getRow(ResultSet rs) throws SQLException, FaildCreateObjectException {
        try {
            boolean filled = false;
            ResultSetMetaData metaData = rs.getMetaData();
            T his = ReflectionUtils.newInstance(this.targetClass);
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                String colName = metaData.getColumnName(i + 1).trim();
                boolean result = setResult(rs, his, colName);
                filled = filled || result;
            }
            return filled ? his : null;
        } catch (IllegalArgumentException | SecurityException e) {
            throw new ApplicationRuntimeException(new SQLExecutionException(e));
        }
    }

    private boolean setResult(ResultSet rs, T model, String colName) throws SQLException {
        log.trace("start setResult");
        log.trace(colName);
        boolean filled = false;
        try {
            PropertyDescriptor property = getPropertyDescriptor(this.targetClass, colName);
            Method writeMethod = property.getWriteMethod();
            Method readMethod = property.getReadMethod();
            Class<?> clazz = writeMethod.getParameterTypes()[0];
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
            } else if (clazz == java.util.Date.class) {
                Timestamp stamp = rs.getTimestamp(colName);
                if (!rs.wasNull()) {
                    java.util.Date result = new Date(stamp.getTime());
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
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | SQLExecutionException e) {
            log.error(e.getMessage(), e);
            throw new ApplicationRuntimeException(new SQLExecutionException(e));
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
    private PropertyDescriptor getPropertyDescriptor(Class<T> clazz, String columnName) throws SQLExecutionException {
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
