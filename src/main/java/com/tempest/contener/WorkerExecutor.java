package com.tempest.contener;

import java.util.concurrent.Callable;

import com.tempest.builder.WorkerMethodDecorator;

import com.tempest.store.State;
import com.tempest.store.Store;

/**
 * 名前は後で変える
 * 
 * 機能は、Taskアノテーションクラスの Workerアノテーションがついたメソッドを解釈したクラス
 * を受け取ってCallableインターフェースでくるんで実行するためのクラス。
 * 
 * なにいってるかわからん。
 */
public class WorkerExecutor<V> implements Callable<V> {

    private WorkerMethodDecorator decorator;

    public WorkerExecutor(WorkerMethodDecorator decorator) {
        this.decorator = decorator;
    }

    @Override
    public V call() throws Exception {
        // TODO: ここで、Storeに領域を確保する。（request Session)みたいな感じ。
        State store = Store.getInstance();
        // Taskインスタンスを取得。
        Object obj = null;

        // decorator.lambda(obj).apply(store);
        // chain.exec(obj).apply(t);
        return null;
    }

}
