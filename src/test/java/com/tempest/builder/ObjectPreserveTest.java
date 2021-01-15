package com.tempest.builder;


import com.tempest.DummyService;
import com.tempest.DummyService2;
import com.tempest.utils.FaildCreateObjectException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ObjectPreserveTest {

    @Test
    public void test() {
        ObjectPreserve p = new ObjectPreserve(DummyService.class, "test", null, null);

        try {
            DummyService s = p.create();
            System.out.println(s.message());
        } catch (FaildCreateObjectException e) {
            e.printStackTrace();
        }

    }
    @Test
    public void test1() {
        ObjectPreserve p = new ObjectPreserve(DummyService.class, "test", null, null);
        ObjectPreserve p2 = new ObjectPreserve(DummyService.class, "test", null, null);
        List<ObjectPreserve> list = new ArrayList<>();
        list.add(p);
        p2.addRelation(list);
        try {
            DummyService2 s = p2.create();
            System.out.println(s.message());
        } catch (FaildCreateObjectException e) {
            e.printStackTrace();
        }

    }
}
