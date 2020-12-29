package com.tempest.task;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

import com.tempest.annotation.TableName;
import com.tempest.sql.Query;
import com.tempest.sql.QueryImpl;
import lombok.extern.log4j.Log4j2;

/**
 * TaskIntercepter
 */
@Log4j2
public class TaskIntercepter implements InvocationHandler {
    private Object target;

    public TaskIntercepter() {
        this.target = Proxy.newProxyInstance(TaskRepository.class.getClassLoader(),
                new Class[] { TaskRepository.class }, this);
    }

    /**
     * 実際のメソッドが実行される前に呼び出されます。
     *
     * @param proxy  元のオブジェクト
     * @param method 実装するメソッド
     * @param args   実行するメソッドの引数
     */
    @Override
    @SuppressWarnings("all")
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (this.log.isDebugEnabled()) {
            this.log.debug("invoke start");
        }

        // Commandアノテーションがついている
        if (this.log.isInfoEnabled()) {
            this.log.info("AOP処理開始");
        }
        try {
            // テーブル名の変更
            Object obj = null;
            // modelがListで渡される場合あり、
            // 引数の中からリストを見つける。
            Object list = Arrays.stream(args).filter(Objects::nonNull).filter(arg -> {
                return Arrays.stream(arg.getClass().getInterfaces()).filter(type -> type == List.class).findAny()
                        .orElse(null) != null;
            }).findAny().orElse(null);
            if (list != null) {
                List tmpList = (List) list;
                if (tmpList.size() > 0) {
                    obj = tmpList.get(0);
                    obj = obj.getClass().getAnnotation(TableName.class) != null ? obj : null;
                }
            } else {
                obj = Arrays.stream(args).filter(Objects::nonNull)
                        .filter(arg -> arg.getClass().getAnnotation(TableName.class) != null).findAny().orElse(null);
            }
            if (obj != null) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("テーブル名変更発生");
                }
                Object query = Arrays.stream(args).filter(Objects::nonNull)
                        .filter(arg -> arg.getClass().getSuperclass() == QueryImpl.class).findAny().orElse(null);
                Class<?> clazz = obj.getClass();
                TableName tableName = clazz.getAnnotation(TableName.class);
                String key = tableName.key();
                String[] names = tableName.names();
                int[] keyValues = tableName.keyValues();
                if (names.length != keyValues.length) {
                    new IllegalArgumentException("件数が不一致");
                }
                Map<Integer, String> map = new HashMap<>();
                for (int i = 0; i < keyValues.length; i++) {
                    map.put(keyValues[i], names[i]);
                }
                PropertyDescriptor property = new PropertyDescriptor(key, clazz);
                int number = (int) property.getReadMethod().invoke(obj, new Object[] {});
                String name = map.get(number);
                if (this.log.isDebugEnabled()) {
                    this.log.debug("name :" + name);
                }
                ((Query) query).setName(name);
            }

            // 引数の一覧を作成
            StringBuilder sb = new StringBuilder();
            if (args != null) {
                Arrays.stream(args).filter(Objects::nonNull).forEach(arg -> sb.append(arg.toString()).append(" "));
            }
            if (this.log.isInfoEnabled()) {
                // メソッド名と引数を出力
                this.log.info("呼び出しメソッド:" + method.getName() + " 引数:" + sb.toString());
            }
        } catch (NullPointerException e) {
            log.warn(e.getMessage(), e);
            throw e;

        }
        try {
            // 実際に実施し、結果を保存する
            log.debug(method.getName());
            log.debug(target.getClass().toString());
            Object result = method.invoke(target, args);

            // 結果がnullでなければ、結果を出力する
            if (log.isDebugEnabled()) {
                if (result != null) {
                    this.log.debug("結果:" + result.toString());
                }
            }
            if (this.log.isInfoEnabled()) {
                this.log.info("AOP処理完了");
            }
            return result;
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw e;
        }

    }

}
