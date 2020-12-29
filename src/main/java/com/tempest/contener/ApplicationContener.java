package com.tempest.contener;

import com.tempest.TaskAccesser;

/**
 * コンテナ
 */
public interface ApplicationContener {

    void addContener(Object annotationInstance);

    /**
     *  
     */
    TaskAccesser search(String name);
}