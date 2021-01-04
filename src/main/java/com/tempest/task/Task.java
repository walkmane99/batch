package com.tempest.task;

import java.util.Optional;

import com.tempest.store.State;
import com.tempest.utils.FaildCreateObjectException;

public interface Task {
    /**
     * 仕事を行います。
     *
     * @param share 共有Bean
     * @return true 仕事を続ける。false 仕事を続けない
     */
    boolean exec(State share) throws InterruptedException;
    /**
    * タスク終了時にやることを記述する。
    */
    void destroy();

    boolean execBefore() throws InterruptedException;



}
