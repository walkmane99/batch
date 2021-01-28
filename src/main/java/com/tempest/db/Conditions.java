package com.tempest.db;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Conditions {

    protected List<Condition<?>> conditions;

    Conditions() {
        this.conditions = new ArrayList<>();
    }

    List<Condition<?>> getConditions() {
        return this.conditions;
    }

    /**
     * 条件の値を登録します。
     *
     * @param name  名前
     * @param value 値
     * @return インスタンス
     */
    public Conditions append(String name, Double value) {
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
    public Conditions append(String name, String value) {
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
    public Conditions append(String name, LocalDate value) {
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
    public Conditions append(String name, Integer value) {
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
    public Conditions append(String name, Long value) {
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
    public Conditions append(String name, List<?> value) {
        this.conditions.add(new ListCondition(name, value));
        return this;
    }

}
