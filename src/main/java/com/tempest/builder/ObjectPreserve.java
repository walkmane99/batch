package com.tempest.builder;

import com.tempest.utils.FaildCreateObjectException;
import com.tempest.utils.ReflectionUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.function.Consumer;

@Data
public class ObjectPreserve {
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
    private boolean isSingleton;

    private Scope score;

    private Class<?> clazz;

    private Object instance;

    private BeanType type;

    private LocalDateTime time;

    public ObjectPreserve(Class<?> clazz, Scope scope, BeanType type ) {
        this(clazz,scope,type,true);
    }
    public ObjectPreserve(Class<?> clazz, Scope scope, BeanType type, boolean isSingleton ) {
        setSingleton(isSingleton);
        setClazz(clazz);
        setScore(scope);
        setType(type);
    }

    @SuppressWarnings("unchecked")
    public <T> T create() throws FaildCreateObjectException {
        if (isSingleton()) {
            if (getInstance() == null) {
                setInstance(newInstance());
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
        setInstance(null);
        setTime(null);
    }


}
