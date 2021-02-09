package com.tempest.builder;

import com.tempest.DummyService;
import com.tempest.DummyService2;
import com.tempest.builder.reflect.ReflectClass;
import com.tempest.utils.FaildCreateObjectException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ObjectPreserveTest {

    @Test
    public void test() {
        ReflectClass n = new ReflectClass();

        ObjectPreserve p = new ObjectPreserve(n.x(DummyService.class), "test", null, null);

        try {
            DummyService s = p.create();
            // Object s = p.create();

            System.out.println(s.getClass().getName());
            System.out.println(s.getClass().getClassLoader());
            System.out.println(s.message());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test1() {
        ReflectClass n = new ReflectClass();
        Class<?> clazz = n.x(DummyService.class);
        ObjectPreserve p = new ObjectPreserve(clazz, "test", null, null);

        ObjectPreserve p2 = new ObjectPreserve(n.x(DummyService2.class), "test", null, null);
        List<ObjectPreserve> list = new ArrayList<>();
        list.add(p);
        p2.addRelation(list);
        try {
            DummyService2 s = p2.create();
            s.setString("str");
            System.out.println(s.getClass().getName());
            System.out.println("aaaaa!:" + s.message());
        } catch (FaildCreateObjectException e) {
            e.printStackTrace();
        }

    }
}
