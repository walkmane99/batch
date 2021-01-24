package com.tempest.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StringCondition implements Condition<String> {

    private String name;

    private String value;

    public StringCondition(String name , String value) {
        this.name = name;
        this.value = value;
    }

  public void set(int index, PreparedStatement ps) throws SQLException {
      ps.setString(index, this.value);
  }

  public String getName() {
        return this.name;
  }
  public String getValue() {
        return this.value;
  }
}
