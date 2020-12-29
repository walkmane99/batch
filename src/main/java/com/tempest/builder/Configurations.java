package com.tempest.builder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.tempest.annotation.Bean;
import com.tempest.utils.FaildCreateObjectException;
import com.tempest.utils.ReflectionUtils;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import lombok.AllArgsConstructor;
import lombok.Data;
import static com.tempest.function.LambdaExceptionUtil.*;

/**
 * Configurations
 *
 * @Configurationsがついているクラスを処理する
 */
public class Configurations {

    private Map<Class<?>, Pair> configurations = new HashMap<>();

    /**
     * コンストラクタ
     *
     * @param classInfoList 「@Configurations」アノテーションのついているクラスのリスト
     */
    Configurations(ClassInfoList classInfoList) {
        init(classInfoList);
    }

    /**
     * 初期処理
     *
     * @param classInfoList 「@Configurations」アノテーションのついているクラスのリスト
     */
    void init(ClassInfoList classInfoList) {
        try {
            for (ClassInfo classInfo : classInfoList) {
                // 作成できなくても続ける。
                this.createConfig(classInfo.loadClass());
            }
        } catch (FaildCreateObjectException | InstantiationException | IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 「@Configurations」アノテーションの付いたクラスからインスタンスを生成
     *
     * @param <T>   生成するクラス型
     * @param clazz インスタンスを生成するクラス
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws FaildCreateObjectException
     */
    private <T> void createConfig(Class<T> clazz)
            throws InstantiationException, IllegalAccessException, FaildCreateObjectException {
        Class<T> cls = clazz;
        T obj = ReflectionUtils.newInstance(cls);
        Stream.of(cls.getMethods()).filter(method -> method.getAnnotation(Bean.class) != null).forEach(method -> {
            Class<?> c = method.getReturnType();
            this.configurations.put(c, new Pair(obj, method));
        });
    }

    /**
     * ＠Beanの付いたメソッドを実行して、Beanを得る。
     *
     * @param bean Bean を保持するクラス
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws ReflectiveOperationException
     */
    void createBean(BeanBuilder bean) throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, ReflectiveOperationException {
        configurations.keySet().stream().forEach(rethrowConsumer(clazz -> createBean(clazz, bean)));
    }

    /**
     * 再帰的にクラスを生成する。
     *
     * @param clazz
     * @param bean
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws ReflectiveOperationException
     */
    Object createBean(Class<?> clazz, BeanBuilder bean) throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, ReflectiveOperationException {
        if (bean.get(clazz) != null) {
            return bean.get(clazz);
        }
        Pair pair = this.configurations.get(clazz);
        Class<?>[] paramClass = pair.getMethod().getParameterTypes();
        if (paramClass.length == 0) {
            Object target = pair.getMethod().invoke(pair.getObj(), new Object[] {});
            bean.set(clazz, target);
            return target;
        } else {
            Object[] array = Stream.of(paramClass).map(rethrowFunction(cls -> createBean(cls, bean))).toArray();
            Object target = pair.getMethod().invoke(pair.getObj(), array);
            bean.set(clazz, target);
            return target;
        }
    }

    @Data
    @AllArgsConstructor
    class Pair {
        private Object obj;
        private Method method;
    }

}
