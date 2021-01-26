package com.tempest.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.Date;

public final class DateCondition implements Condition<LocalDate> {

    private String name;

    private LocalDate value;

    public DateCondition(String name , LocalDate value) {
        this.name = name;
        this.value = value;
    }

  public void set(int index, PreparedStatement ps) throws SQLException {
      ps.setDate(index, Date.valueOf(this.value));
  }

  public String getName() {
        return this.name;
  }
  public LocalDate getValue() {
        return this.value;
  }
}
