package com.tempest;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


/**
 * ServiceManager
 */
public class ServiceManager {

    private static ServiceManager instance;

    private List<Service> list;

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
    public void add(Service object) {
        this.list.add(object);
    }

    /**
     *
     * @param clazz
     * @throws FaildCreateObjectException
     */
    public void createService(Class<?> clazz) throws FaildCreateObjectException {
        try {
            Service service = (Service) clazz.getDeclaredConstructor().newInstance();
            this.add(service);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new FaildCreateObjectException(e);
        }
    }

}
