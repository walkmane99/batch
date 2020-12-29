package com.tempest.builder;

import java.util.HashMap;
import java.util.Map;

import com.tempest.utils.FaildCreateObjectException;
import com.tempest.utils.ReflectionUtils;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ServiceManager {

    private static ServiceManager instance;
    private Map<Class<?>, Object> map;

    public ServiceManager() {
        this.map = new HashMap<>();
    }

    public void addService(Object obj) {
        this.map.put(obj.getClass(), obj);
    }

    public <T> void addService(Class<T> clazz, T obj) {
        this.map.put(clazz, obj);
    }

    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> clazz) {
        return (T) this.map.get(clazz);
    }

    public <T> T creteService(Class<T> clazz)
            throws InstantiationException, IllegalAccessException, FaildCreateObjectException {
        T obj = this.getService(clazz);
        if (obj == null) {
            Class<T> cls = clazz;
            obj = ReflectionUtils.newInstance(cls);
            this.addService(obj);
            this.setAutowired(obj);
        }
        return obj;
    }

    public void setAutowired(Object target) {
        AutowiredResolver autowired = new AutowiredResolver();
        autowired.resolve(target);
    }

    // public void setAutowired(Object target) {
    // if (log.isDebugEnabled()) {
    // log.debug("start setAutowired (Object)");
    // }
    // ;
    // Field[] fields = target.getClass().getDeclaredFields();
    // Arrays.asList(fields).stream().filter(p -> {
    // Autowired autowired = p.getDeclaredAnnotation(Autowired.class);
    // return autowired != null;
    // }).filter(p -> p.getType() != Connection.class).forEach(p -> {
    // if (log.isDebugEnabled()) {
    // log.debug("Field : " + p.getName());
    // }
    // p.setAccessible(true);
    // try {
    // Autowired autowired = p.getDeclaredAnnotation(Autowired.class);
    // Class<?> type = autowired.type();
    // if (log.isDebugEnabled()) {
    // log.debug("create Type : " + type.toString());
    // }
    // Object obj = null;
    // // SQLExecutorを要求しているなら、作成しているものを使う。
    // if (type == SQLExecutorImpl.class) {
    // obj = new SystemSQLExecutor(SQLExecutorImpl.getInstance());
    // } else if (type == FileExecutor.class) {
    // obj = FileExecutor.getInstance();
    // } else {
    // if (autowired.type() != Object.class) {
    // type = autowired.type();
    // }
    // Class<?> cls = type;
    // if (log.isDebugEnabled()) {
    // log.debug("create class " + cls.getName());
    // }
    // obj = ReflectionUtils.newInstance(cls);
    // }
    // // Serviceアノテーションがついているか？
    // Service service = target.getClass().getAnnotation(Service.class);
    // log.debug("設定 target : " + target.getClass().getName());
    // if (service != null && service.proxy() ==
    // com.jfe.base.annotation.Service.Proxy.ON) {
    // log.debug("設定ProxyON: " + obj.getClass().getName());
    // p.set(target, ServiceManager.getProxyInstance(obj));
    // } else {
    // log.debug("設定: " + obj.getClass().getName());
    // p.set(target, obj);
    // }
    // log.debug(() -> "set Autowired. ");
    // } catch (IllegalArgumentException | IllegalAccessException |
    // FaildCreateObjectException e) {
    // log.error(e.getMessage(), e);
    // throw new ApplicationRuntimeException(new FrameworkException(e));
    // }
    // });
    // if (log.isDebugEnabled()) {
    // log.debug("end setAutowired (Object)");
    // }
    // ;
    // }

    public static ServiceManager getInstance() {
        if (instance == null) {
            instance = new ServiceManager();
        }
        return instance;
    }

}
