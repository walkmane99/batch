package com.tempest.function;

import java.util.Arrays;
import java.util.Date;
import java.util.function.Function;

import com.tempest.sql.Query;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class LambdaUtils {

    private LambdaUtils() {

    }

    public static <T extends Comparable<T>> Function<T[], Function<T, Boolean>> equal() {
        return x -> y -> x[0].compareTo(y) == 0;
    }

    public static <T extends Comparable<T>> Function<T[], Function<T, Boolean>> over() {
        return x -> y -> x[0].compareTo(y) < 0;
    }

    public static <T extends Comparable<T>> Function<T[], Function<T, Boolean>> above() {
        return x -> y -> x[0].compareTo(y) <= 0;
    }

    public static <T extends Comparable<T>> Function<T[], Function<T, Boolean>> under() {
        return x -> y -> x[0].compareTo(y) > 0;
    }

    public static <T extends Comparable<T>> Function<T[], Function<T, Boolean>> following() {
        return x -> y -> x[0].compareTo(y) >= 0;
    }

    public static <T extends Comparable<T>> Function<T[], Function<T, Boolean>> over_under() {
        return x -> y -> x[0].compareTo(y) < 0 && x[1].compareTo(y) > 0;
    }

    public static <T extends Comparable<T>> Function<T[], Function<T, Boolean>> over_following() {
        return x -> y -> x[0].compareTo(y) < 0 && x[1].compareTo(y) >= 0;
    }

    public static <T extends Comparable<T>> Function<T[], Function<T, Boolean>> above_under() {
        return x -> y -> x[0].compareTo(y) <= 0 && x[1].compareTo(y) > 0;
    }

    public static <T extends Comparable<T>> Function<T[], Function<T, Boolean>> above_following() {
        return x -> y -> x[0].compareTo(y) <= 0 && x[1].compareTo(y) >= 0;
    }

    public static <T extends Comparable<T>> Function<T[], Function<T, Boolean>> in() {
        return x -> y -> Arrays.asList(x).contains(y);
    }

    public static <T extends Comparable<T>> Function<T[], Function<T, Boolean>> notIn() {
        return x -> y -> !Arrays.asList(x).contains(y);
    }

    /**
     * 配列のコピーを行う関数を返す。
     *
     * @return 配列のコピーを行う関数を返す。
     */
    public static Function<String[], String[]> copyArray() {
        return arry -> {
            String[] distArray = new String[arry.length];
            System.arraycopy(arry, 0, distArray, 0, distArray.length);
            return distArray;
        };
    }

    public static Function<Date[], Function<Date, Boolean>> condition(Query.Symbol symbol) {
        return condition(symbol, Date.class);
    }

    public static Function<Date, Boolean> condition(Query.Symbol symbol, Date[] values) {
        return condition(symbol, Date.class, values);
    }

    public static Function<String, Boolean> condition(Query.Symbol symbol, String[] values) {
        return condition(symbol, String.class, values);
    }

    public static Function<Integer, Boolean> condition(Query.Symbol symbol, Integer[] values) {
        return condition(symbol, Integer.class, values);
    }

    public static <T extends Comparable<T>> Function<T, Boolean> condition(Query.Symbol symbol, Class<T> clazz,
            T[] values) {
        return condition(symbol, clazz).apply(values);
    }

    public static <T extends Comparable<T>> Function<T[], Function<T, Boolean>> condition(Query.Symbol symbol,
            Class<T> clazz) {
        log.trace(() -> "condition start");
        switch (symbol) {
        case IN:
            log.trace(() -> "condition end :IN");
            return in();
        case NOT_IN:
            log.trace(() -> "condition end :NOT_IN");
            return notIn();
        case EQUAL:
            log.trace(() -> "condition end :equal");
            return equal();
        case ABOVE:
            log.trace(() -> "condition end :above");
            return above();
        case UNDER:
            log.trace(() -> "condition end :under");
            return under();
        case FOLLOWING:
            log.trace(() -> "condition end :following");
            return following();
        case OVER:
            log.trace(() -> "condition end :over");
            return over();
        case ABOVE_UNDER:
            log.trace(() -> "condition end :above_under");
            return above_under();
        case OVER_FOLLOWING:
            log.trace(() -> "condition end :over_following");
            return over_following();
        case ABOVE_FOLLOWING:
            log.trace(() -> "condition end :above_following");
            return above_following();
        case OVER_UNDER:
            log.trace(() -> "condition end :over_under");
            return over_under();
        default:
            log.trace(() -> "condition end :default(above_under)");
            return above_under();
        }
    }

}
