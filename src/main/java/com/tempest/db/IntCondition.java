package com.tempest.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class IntCondition implements Condition<Integer> {

    private String name;

    private Integer value;

    public IntCondition(String name , Integer value) {
        this.name = name;
        this.value = value;
    }

  public void set(int index, PreparedStatement ps) throws SQLException {
      ps.setInt(index, this.value);
  }

  public String getName() {
        return this.name;
  }
  public Integer getValue() {
        return this.value;
  }
}
