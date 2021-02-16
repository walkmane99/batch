package com.tempest.builder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.reflect.Parameter;
import com.mysql.cj.x.protobuf.MysqlxCrud.Order;
import com.tempest.annotation.Param;
import com.tempest.annotation.Worker;

import static com.tempest.function.LambdaExceptionUtil.*;

public class TaskBuilder {

    public void m() {

        var methods = new ArrayList<Method>();

        /**
         * Taskアノテーションがあるクラスを見つける。（メソッドに）
         * 
         * メソッドを集める。 アノテーションから名前の確保、 前処理を取得、さらに前処理を取得。
         * 
         * 全体が出来上がったら、実行可能な一連の式に直してRunnableにする。
         * 
         */

        methods.forEach(method -> analyze(method));

    }

    /**
     * 戻りの型をチェックしてどの関数でラップするか決める。
     * 
     * @param method
     */
    public void analyze(Method method) {
        Class<?> clazz = method.getReturnType();
        // 引数の調査

        // paramsにアノテーションがあるかどうか？
        // アノテーションに登録されている名前。

        // 実行後の戻り値は、メソッドの名前で保存

        // 戻り値の調査
        if (clazz == void.class) {
            // 戻り値がないのでConsumer
        } else if (clazz == Stream.class) {
            // 関数化したい
        } else {
            // どうするか？
        }
    }

    interface Anser<T> {

        T get();

    }

    public static class SimpleAnser<T> implements Anser<T> {
        private T obj;

        public SimpleAnser(T obj) {
            this.obj = obj;
        }

        @Override
        public T get() {
            return this.obj;
        }

    }

    // workerアノテーションがついてるメソッドの総数と、chainの長さに矛盾がないか

    public static class MethodChain {

        private String name;

        private Method method;

        private MethodChain parent;

        private String[] necessary;

        MethodChain(Method method) {
            this.method = method;

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

        public void setParent(MethodChain parent) {
            this.parent = parent;
        }

        public String getName() {
            return this.name;
        }

        public void exec() throws Exception {
            var functions = new ArrayList<Function<Anser<?>, ? extends Anser<?>>>();
            functions.stream().reduce((total, f) -> {
                Anser<?> p = f.apply()
            });

        }

        public void exec(List<Function<Anser<?>, ? extends Anser<?>>> functions, Object obj) throws Exception {
            if (this.parent != null) {
                this.parent.exec(functions, obj);

            }

            functions.add(a(obj, this.method.getReturnType()));
        }

        /**
         * このメソッドが前処理の戻り値を必要としているかを確認する。
         * 
         * @return true 前処理の値を必要としている。
         */
        public boolean isParentParameter() {
            return Stream.of(this.getNecessaryParameters()).filter(this.parent.name::equals).count() > 0;

        }

        private <T> Function<Anser<?>, Anser<T>> a(Object object, Class<T> clazz) throws Exception {
            return rethrowFunction((anser) -> {
                try {
                    Object[] args = null;// this.args(anser);
                    return new SimpleAnser<T>((T) this.method.invoke(object, args));
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    throw new Exception(e);
                }
            });
        }

    }

}
