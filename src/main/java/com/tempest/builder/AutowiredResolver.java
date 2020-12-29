package com.tempest.builder;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.tempest.ApplicationRuntimeException;
import com.tempest.annotation.Autowired;

import com.tempest.annotation.Service;
import lombok.extern.log4j.Log4j2;

/**
 * AutowiredResolver
 */
@Log4j2
public class AutowiredResolver {

    /**
     *
     * @param target
     * @return
     */
    private List<Field> getInjectionFields(Object target) {
        Field[] fields = target.getClass().getDeclaredFields();
        return Arrays.asList(fields).stream().filter(p -> {
            Autowired autowired = p.getDeclaredAnnotation(Autowired.class);
            return autowired != null;
        }).filter(p -> p.getType() != Connection.class).collect(Collectors.toList());
    }

    private Class<?> getType(Field field) {
        Autowired autowired = field.getDeclaredAnnotation(Autowired.class);
        if (autowired.type() == Object.class) {
            return field.getType();
        }
        return autowired.type();
    }

    private void set(Object target, Field field) throws IllegalArgumentException, IllegalAccessException {
        field.setAccessible(true);
        Class<?> type = getType(field);
        Object obj = BeanBuilder.getInstance().get(type);
        field.set(target, wrapProxy(target, obj));
        log.debug("target: " + target);
        log.debug("field: " + field.getName());
        log.debug("obj: " + obj);
    }

    private Object wrapProxy(Object target, Object obj) {
        Service service = target.getClass().getAnnotation(Service.class);
        log.debug("設定 target :　" + target.getClass().getName());
        if (service != null && service.proxy() == com.tempest.annotation.Service.Proxy.ON) {
            log.debug("設定ProxyON:　" + obj.getClass().getName());
            return AutowiredResolver.getProxyInstance(obj);
        } else {
            return obj;
        }
    }

    public void resolve(Object target) {
        if (log.isDebugEnabled()) {
            log.debug("start setAutowired (Object)");
        }
        getInjectionFields(target).forEach(p -> {
            if (log.isDebugEnabled()) {
                log.debug("Field : " + p.getName());
            }
            try {
                set(target, p);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                log.error(e.getMessage(), e);
                throw new ApplicationRuntimeException();
            }
        });
        if (log.isDebugEnabled()) {
            log.debug("end setAutowired (Object)");
        }
    }

    public static <T> T getProxyInstance(T instance) {
        Class<? extends Object> clazz = instance.getClass();
        // 対象クラスが実装するインターフェースのリスト
        Class<?>[] classes = clazz.getInterfaces();
        Intercepter intercepter = new Intercepter(instance);
        @SuppressWarnings("unchecked")
        T proxyInstance = (T) Proxy.newProxyInstance(clazz.getClassLoader(), classes, intercepter);
        return proxyInstance;
    }

}
