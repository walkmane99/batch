package com.tempest.function;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * 
 * Represents a value of one of two possible types (a disjoint union.) Instances
 * of Either are either an instance of {@link Left} or {@link Right}
 *
 * @param <LEFT>
 * @param <RIGHT>
 */
public abstract class Either<LEFT, RIGHT> implements Stream<RIGHT> {

    /**
     * The Left version of an Either.
     *
     * @author yanagawa.h
     *
     * @param <LEFT>
     * @param <RIGHT>
     */
    public static class Left<LEFT, RIGHT> extends Either<LEFT, RIGHT> {

        /**
         * Constructs a {@link Left}
         *
         * @param value
         * @return {@link Left}
         */
        public static <LEFT, RIGHT> Left<LEFT, RIGHT> of(LEFT value) {
            if (value == null) {
                throw new NullPointerException("parameter value is null !! ");
            }
            return new Left<>(value);
        }

        final private LEFT left;

        private Left(LEFT value) {
            left = Objects.requireNonNull(value);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;

            return left.equals(((Left<?, ?>) obj).left);
        }

        @Override
        public LEFT getLeftOr(LEFT defaultLeft) {
            return left;
        }

        /**
         * Gets the left value.
         *
         * @return left value.
         */
        @Override
        public LEFT getLeftOrNoSuchElementException() {
            return left;
        }

        @Override
        public RIGHT getOr(RIGHT defaultRight) {
            return defaultRight;
        }

        /**
         * This method throws {@link NoSuchElementException} always.
         *
         * @return right value
         */
        public RIGHT getOrNoSuchElementException() {
            throw new NoSuchElementException("No right object.");
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((left == null) ? 0 : left.hashCode());
            return result;
        }

        /** {@inheritDoc} */
        @Override
        public boolean isLeft() {
            return true;
        }

        /** {@inheritDoc} */
        @Override
        public RIGHT orElseGet(Supplier<? extends RIGHT> other) {
            return other.get();
        }

        /** {@inheritDoc} */
        @Override
        public <X extends Throwable> RIGHT orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
            throw exceptionSupplier.get();
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "Left[" + left + "]";
        }
    }

    /**
     * The Right version of an Either.
     *
     * @author yanagawa.h
     *
     * @param <LEFT>
     * @param <RIGHT>
     */
    public static class Right<LEFT, RIGHT> extends Either<LEFT, RIGHT> {
        /**
         * Constructs a {@link Right}
         *
         * @param value
         * @return {@link Right}
         */
        public static <LEFT, RIGHT> Right<LEFT, RIGHT> of(RIGHT value) {
            if (value == null) {
                throw new NullPointerException("parameter value is null !! ");
            }
            return new Right<>(() -> value);
        }

        final Supplier<RIGHT> rightSupplier;

        private RIGHT rightcache = null;

        /** コンストラクタ */
        private Right(Supplier<RIGHT> rightSupplier) {
            this.rightSupplier = Objects.requireNonNull(rightSupplier);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other)
                return true;
            if (other == null)
                return false;
            if (getClass() != other.getClass())
                return false;

            final RIGHT rThis = getRightSupplier().get();

            @SuppressWarnings("unchecked")
            final RIGHT rOther = ((Right<?, RIGHT>) other).getRightSupplier().get();
            return rThis.equals(rOther);
        }

        @Override
        public LEFT getLeftOr(LEFT defaultLeft) {
            return defaultLeft;
        }

        /**
         * This method throws {@link NoSuchElementException} always.
         *
         * @return left value.
         */
        public LEFT getLeftOrNoSuchElementException() {
            throw new NoSuchElementException("No left object.");
        }

        @Override
        public RIGHT getOr(RIGHT defaultRight) {
            return getRightSupplier().get();
        }

        /**
         * Gets the right value if this is a Right.
         *
         * @return right value
         */
        public RIGHT getOrNoSuchElementException() {
            return getRightSupplier().get();
        }

        /** rightSupplier のキャッシュ(rightSupplierの計算結果を格納しておく) */
        private Supplier<RIGHT> getRightSupplier() {
            if (rightcache == null) {
                rightcache = rightSupplier.get();
            }
            return () -> rightcache;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            final RIGHT r = getRightSupplier().get();
            result = prime * result + ((r == null) ? 0 : r.hashCode());
            return result;
        }

        /** {@inheritDoc} */
        @Override
        public boolean isLeft() {
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public RIGHT orElseGet(Supplier<? extends RIGHT> other) {
            return getRightSupplier().get();
        }

        /** {@inheritDoc} */
        @Override
        public <X extends Throwable> RIGHT orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
            return getRightSupplier().get();
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            final RIGHT right = getRightSupplier().get();
            return "Right[" + right + "]";
        }
    }

    /**
     * Constructs a {@link Left}
     *
     * @param value
     * @return {@link Left}
     */
    public static <LEFT, RIGHT> Left<LEFT, RIGHT> left(LEFT value) {
        return Left.of(value);
    }

    /**
     * Constructs a {@link Right}
     *
     * @param value
     * @return {@link Right}
     */
    public static <LEFT, RIGHT> Right<LEFT, RIGHT> right(RIGHT value) {
        return Right.of(value);
    }

    private Either() {
    }

    /** {@inheritDoc} **/
    @Override
    public boolean allMatch(Predicate<? super RIGHT> predicate) {
        return stream().allMatch(predicate);
    }

    /** {@inheritDoc} **/
    @Override
    public boolean anyMatch(Predicate<? super RIGHT> predicate) {
        return stream().anyMatch(predicate);
    }

    /**
     * This method do Nothing.
     **/
    @Override
    public void close() {
    }

    /** {@inheritDoc} **/
    @Override
    public <R, A> R collect(Collector<? super RIGHT, A, R> collector) {
        return stream().collect(collector);
    }

    /** {@inheritDoc} **/
    @Override
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super RIGHT> accumulator, BiConsumer<R, R> combiner) {
        return stream().collect(supplier, accumulator, combiner);
    }

    /** {@inheritDoc} **/
    @Override
    public long count() {
        return stream().count();
    }

    /** {@inheritDoc} **/
    @Override
    public Stream<RIGHT> distinct() {
        return stream().distinct();
    }

    /** {@inheritDoc} **/
    @Override
    public Stream<RIGHT> filter(Predicate<? super RIGHT> predicate) {
        if (isLeft()) {
            return this;
        }
        return stream().filter(predicate);
    }

    /** {@inheritDoc} **/
    @Override
    public Optional<RIGHT> findAny() {
        return Optional.ofNullable(getOr(null));
    }

    /** {@inheritDoc} **/
    @Override
    public Optional<RIGHT> findFirst() {
        return findAny();
    }

    /** {@inheritDoc} **/
    @Override
    public <R> Stream<R> flatMap(Function<? super RIGHT, ? extends Stream<? extends R>> mapper) {
        return stream().flatMap(mapper);
    }

    public <R> Either<LEFT, R> flatMapEither(Function<? super RIGHT, ? extends Either<LEFT, ? extends R>> mapper) {
        if (isLeft()) {
            @SuppressWarnings("unchecked")
            final Left<LEFT, R> lf = (Left<LEFT, R>) this;
            return lf;
        } else {

            final Either<LEFT, ? extends R> rg = mapper.apply(this.getOrNoSuchElementException());
            if (rg.isLeft()) {
                @SuppressWarnings("unchecked")
                final Left<LEFT, R> lf = (Left<LEFT, R>) rg;
                return lf;
            } else {
                @SuppressWarnings("unchecked")
                final Supplier<? extends R> sp2 = ((Right<?, R>) rg).getRightSupplier();
                final Supplier<R> spRtn = () -> sp2.get();
                return new Right<LEFT, R>(spRtn);
            }
        }
    }

    /** {@inheritDoc} **/
    @Override
    public DoubleStream flatMapToDouble(Function<? super RIGHT, ? extends DoubleStream> mapper) {
        return stream().flatMapToDouble(mapper);
    }

    /** {@inheritDoc} **/
    @Override
    public IntStream flatMapToInt(Function<? super RIGHT, ? extends IntStream> mapper) {
        return stream().flatMapToInt(mapper);
    }

    /** {@inheritDoc} **/
    @Override
    public LongStream flatMapToLong(Function<? super RIGHT, ? extends LongStream> mapper) {
        return stream().flatMapToLong(mapper);
    }

    /** {@inheritDoc} **/
    @Override
    public void forEach(Consumer<? super RIGHT> action) {
        stream().forEach(action);
    }

    /** {@inheritDoc} **/
    @Override
    public void forEachOrdered(Consumer<? super RIGHT> action) {
        stream().forEachOrdered(action);
    }

    /**
     * Gets the left value if this is a Left.
     *
     * @return left value.
     */
    public abstract LEFT getLeftOr(LEFT defaultLeft);

    /**
     * Gets the left value if this is a Left.
     *
     * @return left value.
     */
    public abstract LEFT getLeftOrNoSuchElementException();

    /**
     * Gets the right value if this is a Right.
     *
     * @return right value
     */
    public abstract RIGHT getOr(RIGHT defaultRight);

    /**
     * Gets the right value if this is a Right.
     *
     * @return right value
     */
    public abstract RIGHT getOrNoSuchElementException();

    // ****************TODO
    /**
     * Return the value if {@link Right}, otherwise return {@code other}.
     *
     * @param other the value to be returned if there is no value present, may be
     *              null
     * @return the value, if {@link Right}, otherwise {@code other}
     */
    public RIGHT orElse(RIGHT other) {
        return getOr(other);
    }

    /**
     * Return the value if present, otherwise invoke {@code other} and return the
     * result of that invocation.
     *
     * @param other a {@code Supplier} whose result is returned if no value is
     *              present
     * @return the value if present otherwise the result of {@code other.get()}
     * @throws NullPointerException if value is not present and {@code other} is
     *                              null
     */
    public abstract RIGHT orElseGet(Supplier<? extends RIGHT> other);

    /**
     * Return the contained value, if present, otherwise throw an exception to be
     * created by the provided supplier.
     *
     * @apiNote A method reference to the exception constructor with an empty
     *          argument list can be used as the supplier. For example,
     *          {@code IllegalStateException::new}
     *
     * @param <X>               Type of the exception to be thrown
     * @param exceptionSupplier The supplier which will return the exception to be
     *                          thrown
     * @return the present value
     * @throws X                    if there is no value present
     * @throws NullPointerException if no value is present and
     *                              {@code exceptionSupplier} is null
     */
    public abstract <X extends Throwable> RIGHT orElseThrow(Supplier<? extends X> exceptionSupplier) throws X;

    /**
     * @return true if this is a Left, false otherwise.
     */
    public abstract boolean isLeft();

    /** {@inheritDoc} **/
    @Override
    public boolean isParallel() {
        return false;
    }

    /**
     * @return true if this is a Right, false otherwise.
     */
    public boolean isRight() {
        return !isLeft();
    }

    /** {@inheritDoc} **/
    @Override
    public Iterator<RIGHT> iterator() {
        return stream().iterator();
    }

    private <R> Either<LEFT, R> leftOr(Function<? super RIGHT, ? extends R> mapper) {
        if (isLeft()) {
            @SuppressWarnings("unchecked")
            final Left<LEFT, R> lf = (Left<LEFT, R>) this;
            return lf;
        } else {
            final Right<LEFT, R> rg = Right.of(mapper.apply(this.getOrNoSuchElementException()));
            return rg;
        }
    }

    /** {@inheritDoc} **/
    @Override
    public Stream<RIGHT> limit(long maxSize) {
        if (isLeft()) {
            return this;
        }
        return stream().limit(maxSize);
    }

    /** {@inheritDoc} **/
    @Override
    public <R> Either<LEFT, R> map(Function<? super RIGHT, ? extends R> mapper) {
        return leftOr(mapper);
    }

    /** {@inheritDoc} **/
    @Override
    public DoubleStream mapToDouble(ToDoubleFunction<? super RIGHT> mapper) {
        return stream().mapToDouble(mapper);
    }

    /** {@inheritDoc} **/
    @Override
    public IntStream mapToInt(ToIntFunction<? super RIGHT> mapper) {
        return stream().mapToInt(mapper);
    }

    /** {@inheritDoc} **/
    @Override
    public LongStream mapToLong(ToLongFunction<? super RIGHT> mapper) {
        return stream().mapToLong(mapper);
    }

    /** {@inheritDoc} **/
    @Override
    public Optional<RIGHT> max(Comparator<? super RIGHT> comparator) {
        return stream().max(comparator);
    }

    /** {@inheritDoc} **/
    @Override
    public Optional<RIGHT> min(Comparator<? super RIGHT> comparator) {
        return stream().min(comparator);
    }

    /** {@inheritDoc} **/
    @Override
    public boolean noneMatch(Predicate<? super RIGHT> predicate) {
        return stream().noneMatch(predicate);
    }

    /** {@inheritDoc} **/
    @Override
    public Stream<RIGHT> onClose(Runnable closeHandler) {
        return stream().onClose(closeHandler);
    }

    /** {@inheritDoc} **/
    @Override
    public Stream<RIGHT> parallel() {
        return stream().parallel();
    }

    /** {@inheritDoc} **/
    @Override
    public Stream<RIGHT> peek(Consumer<? super RIGHT> action) {
        return stream().peek(action);
    }

    /** {@inheritDoc} **/
    @Override
    public Optional<RIGHT> reduce(BinaryOperator<RIGHT> accumulator) {
        return stream().reduce(accumulator);
    }

    /** {@inheritDoc} **/
    @Override
    public RIGHT reduce(RIGHT identity, BinaryOperator<RIGHT> accumulator) {
        return stream().reduce(identity, accumulator);
    }

    /** {@inheritDoc} **/
    @Override
    public <U> U reduce(U identity, BiFunction<U, ? super RIGHT, U> accumulator, BinaryOperator<U> combiner) {
        return stream().reduce(identity, accumulator, combiner);
    }

    // FIXME
    // abstract Optional<Supplier<RIGHT>> rightSupplier();

    /** {@inheritDoc} **/
    @Override
    public Stream<RIGHT> sequential() {
        if (isLeft()) {
            return this;
        }
        return stream().sequential();
    }

    /** {@inheritDoc} **/
    @Override
    public Stream<RIGHT> skip(long n) {
        if (isLeft()) {
            return this;
        }
        return stream().skip(n);
    }

    /** {@inheritDoc} **/
    @Override
    public Stream<RIGHT> sorted() {
        if (isLeft()) {
            return this;
        }
        return stream().sorted();
    }

    /** {@inheritDoc} **/
    @Override
    public Stream<RIGHT> sorted(Comparator<? super RIGHT> comparator) {
        if (isLeft()) {
            return this;
        }
        return stream().sorted(comparator);
    }

    /** {@inheritDoc} **/
    @Override
    public Spliterator<RIGHT> spliterator() {
        return stream().spliterator();
    }

    private Stream<RIGHT> stream() {
        if (isLeft()) {
            return Stream.empty();
        } else {
            return Stream.of(getOrNoSuchElementException());
        }
    }

    /** {@inheritDoc} **/
    @Override
    public Object[] toArray() {
        return stream().toArray();
    }

    /** {@inheritDoc} **/
    @Override
    public <A> A[] toArray(IntFunction<A[]> generator) {
        return stream().toArray(generator);
    }

    /** {@inheritDoc} **/
    @Override
    public Stream<RIGHT> unordered() {
        if (isLeft()) {
            return this;
        }
        return stream().unordered();
    }

}
