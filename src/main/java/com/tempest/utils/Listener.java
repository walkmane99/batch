package com.tempest.utils;

import java.util.List;
/**
 * リスナーインターフェース
 */
public interface Listener<T> {
    /**
     *
     */
    void onTrigger(List<T> inData);
}
