package com.tempest.sql;

/**
 * Database
 */
enum Database {

    DB2("com.ibm.db2.jcc.DB2Driver"), POSTGRESQL("org.postgresql.Driver");

    private String driver;

    Database(String driver) {
        this.driver = driver;
    }

    /**
     * @return String return the driver
     */
    public String getDriver() {
        return driver;
    }

    public static Database getDatabase(String driver) {
        if (driver != null) {
            for (Database db : Database.values()) {
                if (db.getDriver().equals(driver)) {
                    return db;
                }
            }
        }
        return DB2;
    }
}