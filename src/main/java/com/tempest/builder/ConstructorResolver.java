package com.tempest.builder;

import com.tempest.annotation.Autowired;
import com.tempest.store.State;
import com.tempest.store.Store;
import com.tempest.utils.FaildCreateObjectException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConstructorResolver {

    public static <T> T newInstance(Constructor<T> constructor) throws FaildCreateObjectException {
        try {
            State store = Store.getInstance();

            Type[] types = constructor.getGenericParameterTypes();
            //Autowiredアノテーションで色々設定していても、設定は無視する

            return (T) constructor.newInstance();
        } catch (Exception e) {
            throw  new FaildCreateObjectException(e);
        }
    }

    /**
     * アノテーションの設定を取得して　その後は決めてない
     * @param autowired
     * @return
     */
    private static Object getHoge(Autowired autowired) {
        if (autowired != null) {
            String name = autowired.name();
            if (!name.equals("")) {
                //
            }
            Class<?> clazz = autowired.type();
        }
        return null;
    }

    public static Object[] getObjects(Type[] types) {
//
//        List<Object> objects = Stream.of(types).forEach(type -> {
//            return null;
//        }).collect(Collectors.toList());
        return null;
    }


}
