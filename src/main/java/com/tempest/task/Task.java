package com.tempest.task;

import com.tempest.store.State;

public interface Task {
    /**
     *
     * @param share 共有Bean
     * @return true 仕事を続ける。false 仕事を続けない
     */
    boolean exec(State share) throws InterruptedException;

    /**
     * タスク終了時にやることを記述する。
     */
    void destroy();

}
