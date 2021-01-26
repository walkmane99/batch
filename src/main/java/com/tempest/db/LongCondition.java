package com.tempest.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class LongCondition implements Condition<Long> {

    private String name;

    private Long value;

    public LongCondition(String name , Long value) {
        this.name = name;
        this.value = value;
    }

  public void set(int index, PreparedStatement ps) throws SQLException {
      ps.setLong(index, this.value);
  }

  public String getName() {
        return this.name;
  }
  public Long getValue() {
        return this.value;
  }
}
