package com.tempest.builder.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.stream.Stream;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.tempest.builder.ObjectPreserve;

import org.apache.logging.log4j.Logger;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.FieldValue;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Configurable;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;

public class ReflectClass {

    // ClassLoader configurable;

    // public ReflectClass() {
    // this.configurable = new MyClassLoader("test",
    // ClassLoadingStrategy.BOOTSTRAP_LOADER);
    // }

    public Class<?> x(Class<?> cls) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        // ClassLoader classLoader = ObjectPreserve.getClassLoader();

        // if (classLoader == null) {
        // classLoader = ClassLoadingStrategy.BOOTSTRAP_LOADER;
        // }

        // 各メソッドに応じて適切なアドバイスを追加する。クラスをそのままに機能を付加する。
        Class<?> clazz = new ByteBuddy().subclass(cls)
                .visit(Advice.to(ForFieldAdvice.class).on(ElementMatchers.named("message"))).make()
                .load(classLoader, ClassLoadingStrategy.Default.WRAPPER).getLoaded();
        return clazz;
    }

    public static class ForFieldAdvice {
        @OnMethodEnter
        public static void enter(@Advice.This Object thisObject, @Advice.Origin String origin,
                @Advice.AllArguments Object[] ary) {

            System.out.println("Inside enter method . . .  ");

            if (ary != null) {
                for (int i = 0; i < ary.length; i++) {
                    System.out.println("Argument: " + i + " is " + ary[i]);
                }
            }

            System.out.println("Origin :" + origin);
            // System.out.println("Detailed Origin :" + detaildOrigin);

            // System.out.println("ForReturnAdvice:field 'string' is " + field);
        }
    }

    // static class MyClassLoader extends ClassLoader {
    // public MyClassLoader(String name, ClassLoader parentLoader) {
    // super(name, parentLoader);
    // }
    // }
}
