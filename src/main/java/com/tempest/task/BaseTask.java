package com.tempest.task;

import java.util.ArrayList;
import java.util.List;

import com.tempest.store.State;

/**
 * タスクの基底クラス
 */
public class BaseTask implements Task {

    /** タスクリスト */
    private final List<Task> list;

    /**
     * コンストラクタ
     */
    public BaseTask() {
        this.list = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    public void add(final Task task) {
        this.list.add(task);
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exec(final State share) throws InterruptedException {
        boolean result = true;

        for (final Task task : this.list) {
            if (!task.exec(share)) {
                result = false;
                break;
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        for (final Task task : this.list) {
            task.destroy();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execBefore() throws InterruptedException {
        return true;
    }
}
