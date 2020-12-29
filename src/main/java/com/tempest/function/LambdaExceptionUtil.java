package com.tempest.function;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class LambdaExceptionUtil {

    @FunctionalInterface
    public interface Consumer_WithExceptions<T, E extends Exception> {
        void accept(T t) throws E;
    }

    @FunctionalInterface
    public interface IntConsumer_WithExceptions<E extends Exception> {
        void accept(int value) throws E;
    }

    @FunctionalInterface
    public interface BiConsumer_WithExceptions<T, U, E extends Exception> {
        void accept(T t, U u) throws E;
    }

    @FunctionalInterface
    public interface Function_WithExceptions<T, R, E extends Exception> {
        R apply(T t) throws E;
    }

    @FunctionalInterface
    public interface BiFunction_WithExceptions<T, U, R, E extends Exception> {
        R apply(T t, U u) throws E;
    }

    @FunctionalInterface
    public interface Supplier_WithExceptions<T, E extends Exception> {
        T get() throws E;
    }

    @FunctionalInterface
    public interface Runnable_WithExceptions<E extends Exception> {
        void run() throws E;
    }

    @FunctionalInterface
    public interface Predicate_WithExceptions<T, E extends Exception> {
        boolean test(T t) throws E;
    }

    /**
     * .forEach(rethrowConsumer(name -> System.out.println(Class.forName(name))));
     * or .forEach(rethrowConsumer(ClassNameUtil::println));
     */
    public static <T, E extends Exception> Consumer<T> rethrowConsumer(Consumer_WithExceptions<T, E> consumer)
            throws E {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
            }
        };
    }

    public static <E extends Exception> IntConsumer rethrowIntConsumer(IntConsumer_WithExceptions<E> consumer)
            throws E {
        return value -> {
            try {
                consumer.accept(value);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
            }
        };
    }

    public static <T, U, E extends Exception> BiConsumer<T, U> rethrowBiConsumer(
            BiConsumer_WithExceptions<T, U, E> biConsumer) throws E {
        return (t, u) -> {
            try {
                biConsumer.accept(t, u);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
            }
        };
    }

    /**
     * .map(rethrowFunction(name -> Class.forName(name))) or
     * .map(rethrowFunction(Class::forName))
     */
    public static <T, R, E extends Exception> Function<T, R> rethrowFunction(Function_WithExceptions<T, R, E> function)
            throws E {
        return t -> {
            try {
                return function.apply(t);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
                return null;
            }
        };
    }

    public static <T, U, R, E extends Exception> BiFunction<T, U, R> rethrowBiFunction(
            BiFunction_WithExceptions<T, U, R, E> function) throws E {
        return (t, u) -> {
            try {
                return function.apply(t, u);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
                return null;
            }
        };
    }

    /**
     * rethrowSupplier(() -> new StringJoiner(new String(new byte[]{77, 97, 114,
     * 107}, "UTF-8"))),
     */
    public static <T, E extends Exception> Supplier<T> rethrowSupplier(Supplier_WithExceptions<T, E> function)
            throws E {
        return () -> {
            try {
                return function.get();
            } catch (Exception exception) {
                throwAsUnchecked(exception);
                return null;
            }
        };
    }

    public static <T, E extends Exception> Predicate<T> rethrowPredicate(Predicate_WithExceptions<T, E> function)
            throws E {
        return (t) -> {
            try {
                return function.test(t);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
                return false;
            }
        };
    }

    public static <E extends Exception> Runnable rethrowRunnable(Runnable_WithExceptions<E> function) throws E {
        return () -> {
            try {
                function.run();
            } catch (Exception exception) {
                throwAsUnchecked(exception);
            }
        };
    }

    /** uncheck(() -> Class.forName("xxx")); */
    @SuppressWarnings("all")
    public static void uncheck(Runnable_WithExceptions t) {
        try {
            t.run();
        } catch (Exception exception) {
            throwAsUnchecked(exception);
        }
    }

    public static <T, E extends Exception> void uncheck(Consumer_WithExceptions<T, E> consumer, T t) {
        try {
            consumer.accept(t);
        } catch (Exception exception) {
            throwAsUnchecked(exception);
        }
    }

    /** uncheck(() -> Class.forName("xxx")); */
    public static <R, E extends Exception> R uncheck(Supplier_WithExceptions<R, E> supplier) {
        try {
            return supplier.get();
        } catch (Exception exception) {
            throwAsUnchecked(exception);
            return null;
        }
    }

    public static <T, E extends Exception> boolean uncheck(Predicate_WithExceptions<T, E> predicate, T t) {
        try {
            return predicate.test(t);
        } catch (Exception exception) {
            throwAsUnchecked(exception);
            return false;
        }
    }

    /** uncheck(Class::forName, "xxx"); */
    public static <T, R, E extends Exception> R uncheck(Function_WithExceptions<T, R, E> function, T t) {
        try {
            return function.apply(t);
        } catch (Exception exception) {
            throwAsUnchecked(exception);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void throwAsUnchecked(Exception exception) throws E {
        throw (E) exception;
    }

}
