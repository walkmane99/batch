package com.tempest.store;

import com.tempest.utils.FaildCreateObjectException;

import java.lang.reflect.Type;
import java.util.Optional;

public interface State {

    <T> Optional<T> getProperties(Type type);
}
