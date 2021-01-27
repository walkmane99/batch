package com.tempest.db;

import java.time.LocalDate;
import java.util.List;

public class Conditions {

    /**
     * 条件の値を登録します。
     *
     * @param name  名前
     * @param value 値
     * @return インスタンス
     */
    public Query append(String name, Double value) {
        this.conditions.add(new DoubleCondition(name, value));
        return this;
    }

    /**
     * 条件の値を登録します。
     *
     * @param name  名前
     * @param value 値
     * @return インスタンス
     */
    public Query append(String name, String value) {
        this.conditions.add(new StringCondition(name, value));
        return this;
    }

    /**
     * 条件の値を登録します。
     *
     * @param name  名前
     * @param value 値
     * @return インスタンス
     */
    public Query append(String name, LocalDate value) {
        this.conditions.add(new DateCondition(name, value));
        return this;
    }

    /**
     * 条件の値を登録します。
     *
     * @param name  名前
     * @param value 値
     * @return インスタンス
     */
    public Query append(String name, Integer value) {
        this.conditions.add(new IntCondition(name, value));
        return this;
    }

    /**
     * 条件の値を登録します。
     *
     * @param name  名前
     * @param value 値
     * @return インスタンス
     */
    public Query append(String name, Long value) {
        this.conditions.add(new LongCondition(name, value));
        return this;
    }

    /**
     * 条件の値を登録します。
     *
     * @param name  名前
     * @param value 値
     * @return インスタンス
     */
    public Query append(String name, List<?> value) {
        this.conditions.add(new ListCondition(name, value));
        return this;
    }


}
