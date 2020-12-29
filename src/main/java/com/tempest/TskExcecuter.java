package com.tempest;

import java.lang.reflect.Method;

import com.tempest.annotation.Autowired;
import com.tempest.store.Store;

/**
 * TskExcecuter
 */
public final class TskExcecuter<T> {

    private final T task;
    private Method runMethod;

    @Autowired
    private Store Store;

    /**
     * このクラスにラップされるクラスは、runアノテーションを持つメソッドが1つあること。
     *
     * @param task
     * @param clazz
     */
    public TskExcecuter(T task, Class<T> clazz) throws Exception {
        this.task = task;
        // このクラスの解析をおこなう。
        // runアノテーションは必須
        // autowird のFieldにDIする
        this.analyze(clazz);
    }

    private void analyze(Class<T> clazz) throws Exception {

    }

    public void exec() {
//        Object[] args = this.getPram();
//        this.runMethod.invoke(this.task, args);
    }
}
