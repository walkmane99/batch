
package com.tempest.db;

import java.sql.SQLException;
import java.util.List;

import com.tempest.db.system.ConnectionPool;
import com.tempest.store.Store;
import com.tempest.utils.FaildCreateObjectException;

import org.junit.Test;

import lombok.Data;

public class DbTest {

    @Test
    public void test1() {
        Query q = new Query("select * from user");
        try {
            List<User> users = q.execute(User.class);
            users.stream().forEach(System.out::println);
        } catch (FaildCreateObjectException | SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void test2() {
        Query q = new Query("select * from user where id = ${id}");
        try {
            q.append("id", 2);
            List<User> users = q.execute(User.class);
            users.stream().forEach(System.out::println);
        } catch (FaildCreateObjectException | SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Data
    public static class User {
        Integer id;
        String username;
        String email;
    }
}