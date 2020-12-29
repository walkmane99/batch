package com.tempest.utils;

import java.util.List;

/**
 *
 */
public abstract class AbstractListener<T> implements Listener<T> {
    /**
     * イベント
     * isActivatedがTrueの場合のみ、イベントを実行します。
     * @param inData 監視対象のデータ
     */
    @Override
    public void onTrigger(List<T> inData) {
        if (this.isActivated(inData)) {
            this.exec(inData);
        }
    }
    /**
     * 発生したイベントを受け取るかどうか判定します。
     * @param inData 監視対象のデータ
     * @return true：受け取る　false:受け取らない
     */
    public abstract boolean isActivated(List<T> inData);
    /**
     * イベント内で実行する具体的な処理を記述します。
     * @param inData 監視対象のデータ
     */
    public abstract void exec(List<T> inData);

}
