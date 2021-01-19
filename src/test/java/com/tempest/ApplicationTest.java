package com.tempest;

import java.util.Optional;

import com.tempest.builder.ObjectPreserveList;
import com.tempest.store.Store;
import com.tempest.utils.FaildCreateObjectException;

import org.junit.Test;

public class ApplicationTest {

    @Test
    public void test() {
        Application app = new Application();

        try {
            app.registerService();
            Store store = (Store) Store.getInstance();
            Optional<ObjectPreserveList> list = store.getProperties(ObjectPreserveList.class);
            ObjectPreserveList s = list.orElseThrow();
            s.analyze();
            DummyService ss = s.get(DummyService.class);
            System.out.println(ss.message());
            DummyService2 ss2 = s.get(DummyService2.class);
            System.out.println(ss2.message());
        } catch (FaildCreateObjectException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
