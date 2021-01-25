package com.tempest.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ListCondition implements Condition<List<?>> {
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
        return null;
    }

    @Override
    public void set(int index, PreparedStatement ps) throws SQLException {

    }
}
