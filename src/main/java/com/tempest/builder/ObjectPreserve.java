package com.tempest.builder;

import com.tempest.utils.FaildCreateObjectException;
import com.tempest.utils.ReflectionUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.function.Consumer;

@EqualsAndHashCode(exclude={"instance","time","isSingleton","scope","type"})
public class ObjectPreserve implements Comparable<ObjectPreserve>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compareTo(ObjectPreserve o) {
        return this.hashCode() - o.hashCode();
    }

    /**
     * インターフェースを考える。
     * イベントの発生？
     *
     */
    public enum BeanType {
        BEAN,COMPONENT,SERVICE;
    }
    public enum Scope {
        SYSTEM,APPLICATION
    }

    @Getter
    private boolean isSingleton;

    @Getter
    private Scope score;

    @Getter
    private Class<?> clazz;

    @Getter
    public String name;

    @Getter
    private Object instance;

    @Getter
    private BeanType type;

    @Getter
    private LocalDateTime time;

    public ObjectPreserve(Class<?> clazz, String name, Scope scope, BeanType type ) {
        this(clazz, name,scope,type,true);
    }
    public ObjectPreserve(Class<?> clazz, String name, Scope scope, BeanType type, boolean isSingleton ) {
        this.clazz = clazz;
        this.name = name;
        this.score = scope;
        this.type = type;
        this.isSingleton = isSingleton;
    }

    @SuppressWarnings("unchecked")
    public <T> T create() throws FaildCreateObjectException {
        if (isSingleton()) {
            if (getInstance() == null) {
                this.instance =newInstance();
                this.time = LocalDateTime.now();
            } else {
                return (T) newInstance();
            }
        }
        return (T)getInstance();
    }

    private Object newInstance()  throws FaildCreateObjectException {
        try {
            Object instance = ConstructorResolver.newInstance( ReflectionUtils.getConstructor(clazz)).orElseThrow();
            injectionAutowired(instance);
            return instance;
        } catch (Throwable throwable) {
            throw new FaildCreateObjectException("create faild.");
        }
    }

    public void injectionAutowired(Object target) {
        AutowiredResolver autowired = new AutowiredResolver();
        autowired.resolve(target);
    }

    public void disponse() {
        this.instance = null;
        this.time = null;
    }


}
