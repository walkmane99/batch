package com.tempest.builder;

import com.tempest.annotation.Autowired;
import com.tempest.store.State;
import com.tempest.store.Store;
import com.tempest.utils.FaildCreateObjectException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.security.auth.x500.X500PrivateCredential;

import static com.tempest.function.LambdaExceptionUtil.*;

public class ConstructorResolver {

    public static <T> Optional<T> newInstance(ObjectPreserve preserve) throws FaildCreateObjectException {
        /** 一番最初にあるコンストラクタを利用する */
        Constructor<?> constructor = preserve.getConstractor()[0];
        try {
            Type[] types = constructor.getGenericParameterTypes();
            // Autowiredアノテーションで色々設定していても、設定は無視する
            Object[] objects = preserve.getObjects(types);
            if (types.length == 0) {
                return Optional.of((T) constructor.newInstance());
            } else if (objects.length == types.length) {
                return Optional.of((T) constructor.newInstance(objects));
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new FaildCreateObjectException(e);
        }
    }

    /**
     * アノテーションの設定を取得して その後は決めてない
     * 
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

}
