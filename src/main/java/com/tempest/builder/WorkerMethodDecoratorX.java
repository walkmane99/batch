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
public interface WorkerMethodDecoratorX {

    // workerアノテーションがついてるメソッドの総数と、chainの長さに矛盾がないか

    boolean canLambdaAndThen(WorkerMethodDecoratorX decorator);

    WorkerMethodDecoratorX lambda(Object obj);

    interface Anser<T> {

        T get();

        Type getType();

        default State getState() {
            return (State) Store.getInstance();
        }

        enum Type {
            VOID, RETURN_VALUE, STATE;
        }
    }

    public static class SimpleAnser<T> implements Anser<T> {
        private T obj;

        private Anser.Type type;

        public SimpleAnser(T obj, Anser.Type type) {
            this.obj = obj;
            this.type = type;
        }

        @Override
        public Anser.Type getType() {
            return this.type;
        }

        @Override
        public T get() {
            return this.obj;
        }

    }

    public class MethodChain {

        private String name;

        private Method method;

        private List<MethodChain> depends;

        private String[] necessary;

        MethodChain(Method method) {
            this.method = method;

        }

        /**
         * 
         * @param list
         */
        public void appendChooseDepends(List<MethodChain> list) {
            var worker = this.method.getAnnotation(Worker.class);
            List<String> depends = Arrays.asList(worker.depends());
            if (depends.size() > 0) {
                this.depends = list.stream().filter(chain -> depends.contains(chain.getName()))
                        .collect(Collectors.toList());
            }
        }

        public String[] getNecessaryParameters() {
            var params = method.getParameters();
            if (this.necessary == null) {
                this.necessary = Stream.of(params).map(param -> {
                    var p = param.getAnnotation(Param.class);
                    if (p != null) {
                        return p.name();
                    }
                    return null;
                }).filter(Objects::nonNull).toArray(String[]::new);

            }
            return this.necessary;
        }

        public String getName() {
            if (this.name == null) {
                this.name = this.method.getAnnotation(Worker.class).name();
            }
            return this.name;
        }

        public Function<Anser<?>, ? extends Anser<?>> exec(Object obj) throws Exception {
            Class<?> clazz = this.method.getReturnType();
            var x = a(obj, clazz);
            if (this.depends != null) {
                for (MethodChain chain : this.depends) {
                    x.andThen(chain.exec(obj));
                }
            }
            return x;
        }

        /**
         * 
         * @param <T>
         * @param object
         * @param clazz
         * @return
         * @throws Exception
         */
        private <T> Function<Anser<?>, Anser<?>> a(Object object, Class<T> clazz) throws Exception {
            return rethrowFunction((anser) -> {
                try {
                    Object[] args = null;// this.args(anser);
                    Object value = null;
                    State state = anser.getState();
                    if (anser.getType() != Anser.Type.VOID) {

                    } else if (anser.getType() != Anser.Type.RETURN_VALUE) {
                        value = anser.get();
                    }
                    if (this.method.getReturnType() == void.class) {
                        this.method.invoke(object, args);
                        return new SimpleAnser<Integer>(null, Anser.Type.VOID);
                    }
                    return new SimpleAnser<T>((T) this.method.invoke(object, args), Anser.Type.RETURN_VALUE);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    throw new Exception(e);
                }
            });
        }

        public boolean isParentParameter() {

            /**
             * Taskアノテーションがあるクラスを見つける。（メソッドに）
             * 
             * メソッドを集める。 アノテーションから名前の確保、 前処理を取得、さらに前処理を取得。
             * 
             * 全体が出来上がったら、実行可能な一連の式に直してRunnableにする。
             * 
             */
            var list = Arrays.asList(this.getNecessaryParameters());
            return this.depends.stream().filter(chain -> list.contains(chain.getName())).count() > 0;
        }

    }

    /**
     * 
     * @param methods クラスのメソッド(workerアノテーションがついたもの)
     * @param name
     */
    public default MethodChain create(List<Method> methods, String name) {
        List<MethodChain> methodChains = methods.stream().map(method -> new MethodChain(method))
                .collect(Collectors.toList());
        MethodChain result = null;
        for (MethodChain chain : methodChains) {
            chain.appendChooseDepends(methodChains);
            if (chain.getName().equals(name)) {
                result = chain;
            }
        }
        return result;
        // Function<Anser<?>, ? extends Anser<?>> exec(obj);
    }

}