package com.tempest.sql;
import java.util.List;

/**
 * Columnable
 */
public interface Columnable {
    public List<String> getColumns();

    public void setColumns(List<String> list);
}
