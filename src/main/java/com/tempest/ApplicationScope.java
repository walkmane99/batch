package com.tempest;

import java.util.HashMap;
import java.util.Map;

/**
 * ApplicationScope アプリケーションレベルで値を保持するStateクラス。
 */
public class ApplicationScope {

    private static ApplicationScope instance;

    public Map<String, Object> map;

    private ApplicationScope() {
        map = new HashMap<>();
    }

    public static ApplicationScope getInstance() {
        if (instance == null) {
            instance = new ApplicationScope();
        }
        return instance;
    }

    public void put(String key, Object obj) {
        this.map.put(key, obj);
    }

    public <T> T get(String key) {
        return this.get(key);
    }
}