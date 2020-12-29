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

    /**
     * 各モジュールで独自に作成したBeanクラスを取得する。
     *
     * application.confに、「system.properties」を作成し、パッケージ名を含むクラス名を設定する。
     * 独自に作成したBeanは、Stateクラスを継承する必要はない。
     *
     * @param <T>   戻りの型
     * @param share 共有オブジェクト
     * @return 独自に作成したオブジェクト
     */
    public default <T> Optional<T> getProperties(State share) {
        try {
            return Optional.of(share.getProperties());
        } catch (FaildCreateObjectException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(null);
    }

}
