package com.tempest.builder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.tempest.ApplicationRuntimeException;
import com.tempest.utils.FaildCreateObjectException;
import com.tempest.utils.ReflectionUtils;

import io.github.classgraph.ClassInfoList;
import lombok.extern.log4j.Log4j2;

import static com.tempest.function.LambdaExceptionUtil.*;

@Log4j2
public class ServiceManager {

    private static ServiceManager instance;
    private Map<Type, Object> map;

    /**
     * コンストラクタ
     */
    private ServiceManager() {
        this.map = new HashMap<>();
    }

    /**
     * インスタンスを取得します。
     * 
     * @return
     */
    public static ServiceManager getInstance() {
        if (instance == null) {
            instance = new ServiceManager();
        }
        return instance;
    }

    public void addService(Object obj) {
        this.map.put(obj.getClass().getComponentType(), obj);
    }

    public <T> void addService(Class<T> clazz, T obj) {
        this.map.put(clazz, obj);
    }

    @SuppressWarnings("unchecked")
    public <T> T getService(Type clazz) {
        return (T) this.map.get(clazz);
    }

    public void injectionAutowired(Object target) {
        AutowiredResolver autowired = new AutowiredResolver();
        autowired.resolve(target);
    }

}
