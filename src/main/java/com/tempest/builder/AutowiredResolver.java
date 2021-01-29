package com.tempest.builder;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public List<Field> getInjectionFields(Class<?> target) {
        Field[] fields = target.getDeclaredFields();
        return Stream.of(fields).filter(p -> {
            Autowired autowired = p.getDeclaredAnnotation(Autowired.class);
            return autowired != null;
        }).collect(Collectors.toList());
    }

    /**
     *
     * @param target
     * @return
     */
    private List<Field> getInjectionFields(Object target) {
        return this.getInjectionFields(target.getClass());
    }

    private Type getType(Field field) {
        Autowired autowired = field.getDeclaredAnnotation(Autowired.class);
        if (autowired.type() == Object.class) {
            return field.getType();
        }
        return autowired.type();
    }

    private void set(Object target, Field field) throws IllegalArgumentException, IllegalAccessException {
        field.setAccessible(true);
        Type type = getType(field);
        Object obj = BeanBuilder.getInstance().get(type);
        log.debug("target: " + target);
        log.debug("field: " + field.getName());
        log.debug("obj: " + obj);
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

}
