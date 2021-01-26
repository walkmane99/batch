package com.tempest.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class DoubleCondition implements Condition<Double> {

    private String name;

    private Double value;

    public DoubleCondition(String name , Double value) {
        this.name = name;
        this.value = value;
    }

  public void set(int index, PreparedStatement ps) throws SQLException {
      ps.setDouble(index, this.value);
  }

  public String getName() {
        return this.name;
  }
  public Double getValue() {
        return this.value;
  }
}
