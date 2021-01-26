package com.tempest.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public final class ListCondition implements Condition<List<?>> {
    private String name;

    private List<?> value;

    public ListCondition(String name , List<?> value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<?> getValue() {
        return this.value;
    }

    @Override
    public void set(int index, PreparedStatement ps) throws SQLException {

    }
}
