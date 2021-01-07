package com.tempest.utils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


import com.tempest.ApplicationRuntimeException;
import com.tempest.InvokeException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import static com.tempest.function.LambdaExceptionUtil.*;

/**
 * リフレクションを行う際の便利メソッドを集めました。 ReflectionUtils
 */
@Log4j2
public class ReflectionUtils {

    private ReflectionUtils() {
    }

    /**
     * インスタンスの作成を行います。
     *
     * @param clazz インスタンスを作成するクラス。
     * @exception FaildCreateObjectException インスタンスが作成できない場合。
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<T> clazz) throws FaildCreateObjectException {
        try {
            Class<T> targetClazz = (Class<T>) Thread.currentThread().getContextClassLoader().loadClass(clazz.getName());
            return targetClazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
            throw new FaildCreateObjectException("インスタンスの作成に失敗", e);
        }
    }

    /**
     * List<String>をList<T>に変換します。
     *
     * @param values 変換対象
     * @param clazz  変換するクラス
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> convertList(List<String> values, Class<T> clazz) {
        List<T> list = values.stream().map(var -> (T) convert(var, clazz)).collect(Collectors.toList());
        return list;
    }

    /**
     * Beanを指定して、文字列で指定された値を取得して返します。<br>
     *
     *
     * @param bean         対象のBean
     * @param propertyName プロパティ名。(.)で連結された文字列もOK
     * @param type         戻り値のtype
     */
    @SuppressWarnings("unchecked")
    public static <T, R> R getBeanValue(T bean, String propertyName, Class<R> type) throws IllegalAccessException {
        log.debug(() -> "start getBeanValue");
        String[] elements = null;
        if (propertyName.indexOf(".") <= -1) {
            elements = new String[] { propertyName };
            log.debug(propertyName);
        } else {
            elements = new String[2];
            elements[0] = propertyName.substring(0, propertyName.indexOf("."));
            log.debug(elements[0]);
            elements[1] = propertyName.substring(propertyName.indexOf(".") + 1);
            log.debug(elements[1]);
        }
        Object result = null;
        try {
            if (elements.length > 0) {
                if (bean instanceof Map) {
                    Method method;
                    method = Map.class.getMethod("get", new Class<?>[] { Object.class });
                    result = method.invoke(bean, new Object[] { elements[0] });
                } else {
                    PropertyDescriptor descriptor = getPropertyDescriptor(bean.getClass(), elements[0]);
                    Method method = descriptor.getReadMethod();
                    result = method.invoke(bean, new Object[] {});
                    if (elements.length > 1 && result != null) {
                        result = getBeanValue(result, elements[1], Object.class);
                    }
                }
            }
            // Stringにする。
            if (result != null) {
                String value = toString(result);
                return (R) convert(value, type);
            }
        } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            log.catching(Level.DEBUG, e);
        }
        log.debug(() -> "end getBeanValue");
        return (R) result;
    }

    /**
     * Stringを<T>に変換し、オブジェクトを返します。
     *
     * @param value 変換対象
     * @param clazz 変換するクラス
     */
    public static Object convert(String value, Class<?> clazz) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        if (clazz == Boolean.class) {
            return Boolean.valueOf(value);
        } else if (clazz == Date.class) {
            return DateUtils.getFullDate(value);
        } else if (clazz == int.class || clazz == Integer.class) {
            return Integer.parseInt(value);
        } else if (clazz == long.class || clazz == Long.class) {
            return Long.parseLong(value);
        } else if (clazz == Float.class || clazz == float.class) {
            return Float.parseFloat(value);
        } else if (clazz == Double.class || clazz == double.class) {
            return Double.parseDouble(value);
        }
        return value;
    }

    public static String toString(Object object, Method method) throws InvokeException {
        try {
            return ReflectionUtils.toString(method.invoke(object, new Object[] {}));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new InvokeException(e);
        }
    }

    public static String toString(Object value) {
        if (value instanceof String) {
            return toString((String) value);
        } else if (value instanceof Date) {
            return toString((Date) value);
        } else if (value instanceof Integer) {
            return toString((Integer) value);
        } else if (value instanceof Long) {
            return toString((Long) value);
        } else if (value instanceof Float) {
            return toString((Float) value);
        } else if (value instanceof Double) {
            return toString((Double) value);
        }
        return "";
    }

    public static String toString(String value) {
        return value;
    }

    public static String toString(Date value) {
        return DateUtils.dateToStringJapanese(value);
    }

    public static String toString(Integer value) {
        return String.valueOf(value);
    }

    public static String toString(Long value) {
        return String.valueOf(value);
    }

    public static String toString(Double value) {
        return BigDecimal.valueOf(value).toPlainString();
        // return String.valueOf(value);
    }

    public static String toString(Float value) {
        return BigDecimal.valueOf(value).toPlainString();
        // return String.valueOf(value);
    }

    /**
     * List<String>をList<T>に変換します。
     *
     * @param values 変換対象
     * @param clazz  変換するクラス
     */
//    public static <T> List<String> convertList(T values, MetaData data, Class<T> clazz) {
//        return data.getList().stream().map(x -> {
//            try {
//                PropertyDescriptor descriptor = getPropertyDescriptor(clazz, x);
//                Method read = descriptor.getReadMethod();
//                Object obj;
//                try {
//                    obj = read.invoke(values, new Object[] {});
//                    return obj.toString();
//                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//                }
//            } catch (ApplicationRuntimeException e) {
//
//            }
//            return " ";
//        }).collect(Collectors.toList());
//    }

    /**
     * PropertyDescriptorを返します。
     *
     * @param clazz      クラス
     * @param name カラム名
     *
     * @return PropertyDescriptor
     */
    public static <T> PropertyDescriptor getPropertyDescriptor(Class<T> clazz, String name) {
        try {
            return new PropertyDescriptor(name, clazz);
        } catch (IntrospectionException e) {
            // if (log.isDebugEnabled()) {
            // log.debug("end getPropertyDescriptor", e);
            // }
            throw new ApplicationRuntimeException(new FaildCreateObjectException(e));
        }
    }

    /**
     * クラスのプロパティ(Field)を取得します。
     *
     * スーパクラスのプロパティも取得します。
     *
     * @param clazz 対象のクラス。
     * @return Fieldの配列
     */
    public static <T> Field[] getFields(Class<T> clazz) {
        Class<?> superClazz = clazz.getSuperclass();
        Field[] superFields = null;
        if (superClazz != null) {
            superFields = getFields(superClazz);
        }
        Field[] fields = clazz.getDeclaredFields();
        if (fields == null) {
            return superFields;
        } else if (superFields == null) {
            return fields;
        }

        Field[] result = new Field[superFields.length + fields.length];
        System.arraycopy(superFields, 0, result, 0, superFields.length);
        System.arraycopy(fields, 0, result, superFields.length, fields.length);
        return result;
    }



    /**
     * 実際にインスタンスに詰め込む処理を行う関数を返します。
     *
     * @param procNo   プロセス番号
     * @param uniqueId ユニークID
     * @param metaData メタデータ
     * @return インスタンスに詰め込む関数。
     */
//    public static <T> Function<Item, BiConsumer_WithExceptions<T, Record, Exception>> fillInstance(Integer procNo,
//            String uniqueId, MetaData metaData) {
//        return x -> (rs, record) -> {
//            Field field = x.getField();
//            Inject inject = field.getDeclaredAnnotation(Inject.class);
//            if (inject != null) {
//                inject(field, inject, metaData).apply(x).accept(rs, record);
//            } else {
//                // List以外のパラメータ
//                if (field.getName().equals("procNo")) {
//                    x.getMethod().invoke(rs, new Object[] { procNo });
//                } else if (field.getName().equals("uniqueId")) {
//                    x.getMethod().invoke(rs, new Object[] { uniqueId });
//                } else {
//                    // 戻すクラスに定義されているフィールド名と同じ名前のついたCSVカラムを返す。
//                    try {
//                        String value = record.getValue(ConditionsUtils.camelToSsnake(field.getName()));
//                        if (log.isTraceEnabled()) {
//                            log.trace("field name : " + ConditionsUtils.camelToSsnake(field.getName()));
//                        }
//                        x.getMethod().invoke(rs, new Object[] { ReflectionUtils.convert(value, field.getType()) });
//                    } catch (NotFoundException e) {
//                    }
//                }
//            }
//        };
//    }

    /**
     * 実際にインスタンスに詰め込む処理を行う関数を返します。
     *
     * @param procNo   プロセス番号
     * @param uniqueId ユニークID
     * @param metaData メタデータ
     * @return インスタンスに詰め込む関数。
     */
//    public static <T> Function<Item, BiConsumer_WithExceptions<T, Record, Exception>> fillInstanceAll(Integer procNo,
//            String uniqueId, MetaData metaData) {
//        return x -> (rs, record) -> {
//            Field field = x.getField();
//            Inject inject = field.getDeclaredAnnotation(Inject.class);
//            if (inject != null) {
//                inject(field, inject, metaData).apply(x).accept(rs, record);
//            } else {
//                // 戻すクラスに定義されているフィールド名と同じ名前のついたCSVカラムを返す。
//                try {
//                    String value = record.getValue(ConditionsUtils.camelToSsnake(field.getName()));
//                    if (log.isTraceEnabled()) {
//                        log.trace("field name : " + ConditionsUtils.camelToSsnake(field.getName()));
//                    }
//                    x.getMethod().invoke(rs, new Object[] { ReflectionUtils.convert(value, field.getType()) });
//                } catch (NotFoundException e) {
//                }
//            }
//        };
//    }

//    private static <T> Function<Item, BiConsumer_WithExceptions<T, Record, Exception>> inject(Field field,
//            Inject inject, MetaData metaData) {
//        return x -> (rs, record) -> {
//            if (field.getType() == List.class) {
//                if (inject.type() == Inject.Type.KEY) {
//                    fillHeadList(metaData).apply(x).accept(rs, record);
//                } else {
//                    fillValueList(metaData).apply(x).accept(rs, record);
//                }
//            } else {
//                ParameterizedType pt = (ParameterizedType) field.getGenericType();
//                Type[] types = pt.getActualTypeArguments();
//                if (types[0] != String.class) {
//                    throw new Exception("Mapのジェネリックスの定義が間違っています。");
//                }
//                if (inject.type() == Inject.Type.KEY) {
//                    fillHeadMap(metaData).apply(x).accept(rs, record);
//                } else {
//                    fillValueMap(metaData, (Class<?>) types[1]).apply(x).accept(rs, record);
//                }
//            }
//        };
//    }

    /**
     * ラベルをMapにして用意されたFieldに詰め込みます。
     *
     * @param <T>
     * @param metaData
     * @return
     */
//    private static <T> Function<Item, BiConsumer_WithExceptions<T, Record, Exception>> fillHeadMap(MetaData metaData) {
//        // List データ領域
//        // メタデータに問い合わせる。
//        List<String> list = metaData.getSensors();
//        return x -> (rs, record) -> {
//            x.getMethod().invoke(rs, new Object[] { IntStream.range(0, list.size()).boxed()
//                    .collect(Collectors.toMap(list::get, Function.identity())) });
//        };
//    }

    /**
     * ラベルをListにして用意されたFieldに詰め込みます。
     *
     * @param <T>
     * @param metaData
     * @return
     */
//    private static <T> Function<Item, BiConsumer_WithExceptions<T, Record, Exception>> fillHeadList(MetaData metaData) {
//        // List データ領域
//        // メタデータに問い合わせる。
//        return x -> (rs, record) -> {
//            List<String> sensorVals = metaData.getSensors().stream().map(Function.identity())
//                    .collect(Collectors.toList());
//            x.getMethod().invoke(rs, new Object[] { sensorVals });
//        };
//    }

    /**
     * 値をリストに詰め込みます。
     *
     * @param <T>
     * @param metaData
     * @return
     */
//    private static <T> Function<Item, BiConsumer_WithExceptions<T, Record, Exception>> fillValueList(
//            MetaData metaData) {
//        // List データ領域
//        // メタデータに問い合わせる。
//        return x -> (rs, record) -> {
//            List<String> sensorVals = metaData.getSensors().stream().map(sensorName -> {
//                try {
//                    if (log.isTraceEnabled()) {
//                        log.trace("センサー名" + sensorName);
//                    }
//                    return record.getValue(sensorName);
//                } catch (NotFoundException e) {
//                    log.warn(String.format("noting sensor , %s", sensorName));
//                }
//                return null;
//            }).filter(Objects::nonNull).collect(Collectors.toList());
//            Type[] types = x.getMethod().getGenericParameterTypes();
//            // Now assuming that the first parameter to the method is of type List<Integer>
//            ParameterizedType pType = (ParameterizedType) types[0];
//            Class<?> genericClass = (Class<?>) pType.getActualTypeArguments()[0];
//            x.getMethod().invoke(rs, new Object[] { ReflectionUtils.convertList(sensorVals, genericClass) });
//        };
//    }

    /**
     * 値をMapに詰め込みます。
     *
     * @return
     */
//    private static <T> Function<Item, BiConsumer_WithExceptions<T, Record, Exception>> fillValueMap(MetaData metaData,
//            Class<?> genericClass) {
//        // List データ領域
//        // メタデータに問い合わせる。
//        return x -> (rs, record) -> {
//            Map<String, Object> sensorVals = metaData.getSensors().stream().map(sensorName -> {
//                try {
//                    if (log.isTraceEnabled()) {
//                        log.trace("センサー名" + sensorName);
//                    }
//                    return new KeyValue(sensorName, ReflectionUtils.convert(record.getValue(sensorName), genericClass));
//                } catch (NotFoundException e) {
//                    log.warn(String.format("noting sensor , %s", sensorName));
//                }
//                return null;
//            }).filter(Objects::nonNull).filter(e -> e.getKey() != null && !e.getKey().isEmpty())
//                    .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue() == null ? "" : e.getValue(),
//                            (e1, e2) -> e1, LinkedHashMap::new));
//            x.getMethod().invoke(rs, new Object[] { sensorVals });
//        };
//    }

    @Data
    @AllArgsConstructor
    static class KeyValue {
        String key;
        Object value;
    }
}
