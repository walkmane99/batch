package com.tempest.builder;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import com.tempest.db.system.ConnectionPool;
import com.tempest.utils.FaildCreateObjectException;
import com.tempest.utils.ReflectionUtils;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import lombok.extern.log4j.Log4j2;
import static com.tempest.function.LambdaExceptionUtil.*;

/**
 * BeanBuilder
 */
@Log4j2
public class BeanBuilder {

    private static BeanBuilder instance;
    private AutowiredResolver autowired;
    private Map<Type, Object> beans = new HashMap<>();

    private BeanBuilder() {
        autowired = new AutowiredResolver();
    }

    public static BeanBuilder getInstance() {
        if (instance == null) {
            instance = new BeanBuilder();
            instance.search("com.jfe.base.annotation.Configuration", builderConfigurations());
            instance.search("com.jfe.base.annotation.Bean", builderBeans());
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Type type) {
        log.debug("aaaaaaaa: " + type);
        T target = (T) this.beans.get(type);
        if (target != null) {
            autowired.resolve(target);
            return target;
        }
        return null;
    }

    void set(Type clazz, Object obj) {
        this.beans.put(clazz, obj);
    }

    private static BiConsumer<BeanBuilder, ClassInfoList> builderConfigurations() {
        return (bean, classInfoList) -> {
            try {
                Configurations configurations = new Configurations(classInfoList);
                configurations.createBean(bean);
            } catch (IllegalArgumentException | ReflectiveOperationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        };
    }

    private static BiConsumer<BeanBuilder, ClassInfoList> builderBeans() {
        return (bean, classInfoList) -> {
            try {
                classInfoList.stream().forEach(rethrowConsumer(classInfo -> {
                    Class<?> cls = classInfo.loadClass();
                    Object obj = ReflectionUtils.newInstance(cls);
                    bean.set(cls, obj);
                    Stream.of(cls.getInterfaces()).forEach(c -> bean.set(c, obj));
                }));
            } catch (IllegalArgumentException | FaildCreateObjectException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        };
    }

    private void search(String annotation, BiConsumer<BeanBuilder, ClassInfoList> create) {
        log.trace(() -> "search start");

        try (ScanResult scanResult = new ClassGraph().enableAllInfo() // Scan classes, methods, fields,
                                                                      // annotations
                .scan()) {
            ClassInfoList classInfoList = scanResult.getClassesWithMethodAnnotation(annotation);
            try {
                create.accept(this, classInfoList);
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            log.trace(() -> "search end");
        }
    }

}
