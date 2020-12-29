package com.tempest.task;


/**
 * 抽象化されたTaskです。
 *
 */
public interface TransactionalTask extends Task {
    /**
     * ロールバックする
     */
    void rollback();
    /**
     * コミットする。
     */
    void commit();
}
