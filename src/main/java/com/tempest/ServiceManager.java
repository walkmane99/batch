package com.tempest;

import com.tempest.utils.ConstructorResolver;
import com.tempest.utils.FaildCreateObjectException;
import com.tempest.utils.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


/**
 * ServiceManager
 */
public class ServiceManager {

    private static ServiceManager instance;

    private List<Object> list;

    private ServiceManager() {
        this.list = new ArrayList<>();
    }

    /**
     *
     * @return
     */
    public static ServiceManager getInstance() {
        if (ServiceManager.instance == null) {
            ServiceManager.instance = new ServiceManager();
        }
        return ServiceManager.instance;
    }

    /**
     *
     * @param object
     */
    public void add(Object object) {
        this.list.add(object);
    }

    /**
     *
     * @param clazz
     * @throws FaildCreateObjectException
     */
    public void createService(Class<?> clazz) throws FaildCreateObjectException {
            // コンストラクタを取得。
           Constructor<?> constructor = ReflectionUtils.getConstructor(clazz);
        Object obj =  ConstructorResolver.newInstance(constructor);
        this.add(obj);
    }

}
