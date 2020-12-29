package com.tempest.store;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Store<br>
 * 
 * このクラスでアプリケーション動作中全ての状態を保持するクラスです。
 * 
 * 状態はJsonとして保存し、更新要求の際、変化をチェック。 変化が確認されると、それを入力として待つオブジェクトを呼び出します。
 * 
 */
public class Store {

    private List<PropertyChangeListener> listeners;

    private Map<String, Object> map;

    public Store() {
        this.listeners = new ArrayList<>();
        this.map = new HashMap<>();
    }

    public void put(String key, Object value) {
        this.map.put(key, value);
    }

    /**
     * 指定したキーが存在する場合trueを返します。
     * 
     * @param key
     * @return
     */
    public boolean hasObject(String key) {
        return map.containsKey(key);
    }

    // 保持しているオブジェクトが持っているアノテーションを探して返すメソッド

    public List<Object> getObject(String name) {
        return map.values().stream().map(x -> {
            Class<?> clazz = x.getClass();
            long count = Stream.of(clazz.getDeclaredAnnotations()).filter(a -> a.toString().equals(name)).count();
            if (count > 0) {
                return x;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.listeners.add(listener);
    }

    public void onChance(PropertyChangeEvent evt) {
        this.listeners.forEach(listener -> listener.propertyChange(evt));
    }

}