package com.tempest.builder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.tempest.annotation.Param;
import com.tempest.annotation.Worker;
import com.tempest.store.State;
import com.tempest.store.Store;

import static com.tempest.function.LambdaExceptionUtil.*;

/**
 * メソッドを扱いやすいようにするインターフェースを考える。
 * 
 * 
 */
public interface WorkerMethodDecorator<T> {

    // workerアノテーションがついてるメソッドの総数と、chainの長さに矛盾がないか

    /**
     * 関数合成可能か？
     * 
     * @param decorator
     * @return
     */
    boolean canLambdaAndThen(WorkerMethodDecorator<T> decorator);

    T lambda(Object obj);

    /**
     * 
     * @param methods クラスのメソッド(workerアノテーションがついたもの)
     * @param name
     */
    public default WorkerMethodDecorator<T> create(List<Method> methods, String name) {
        return null;
    }

    class ConsumerWorkerMethod<T> implements WorkerMethodDecorator<Consumer<T>> {

        @Override
        public boolean canLambdaAndThen(WorkerMethodDecorator<Consumer<T>> decorator) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public Consumer<T> lambda(Object obj) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    interface Anser<E> {

        E get();

        Type getType();

        default State getState() {
            return (State) Store.getInstance();
        }

        enum Type {
            VOID, RETURN_VALUE, STATE;
        }
    }

    public static class SimpleAnser<E> implements Anser<E> {
        private E obj;

        private Anser.Type type;

        public SimpleAnser(E obj, Anser.Type type) {
            this.obj = obj;
            this.type = type;
        }

        @Override
        public Anser.Type getType() {
            return this.type;
        }

        @Override
        public E get() {
            return this.obj;
        }

    }

}