package com.tempest.builder;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ObjectPreserve {

    public enum BeanType {
        BEAN,COMPONENT,SERVICE;
    }
    public enum Scope {
        SYSTEM,APPLICATION
    }
    private boolean isSingleton = false;

    private Scope score;

    private Class<?> clazz;

    private Object instance;

    private BeanType type;

}
