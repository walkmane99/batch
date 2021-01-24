package com.tempest.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public interface Condition<T> {

    String getName();
    T getValue();
    void set(int index, PreparedStatement ps) throws SQLException;
}
