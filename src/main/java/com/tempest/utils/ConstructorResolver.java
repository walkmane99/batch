package com.tempest.utils;

import com.tempest.store.State;
import com.tempest.store.Store;

import java.lang.reflect.Constructor;

public class ConstructorResolver {

    public static <T> T newInstance(Constructor<T> constructor) throws FaildCreateObjectException {
        try {
            State store = Store.getInstance();
            return (T) constructor.newInstance();
        } catch (Exception e) {
            throw  new FaildCreateObjectException(e);
        }
    }



}
