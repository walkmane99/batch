package com.tempest.task;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.tempest.annotation.Autowired;
import com.tempest.db.system.ConnectionPool;
import com.tempest.store.State;
import org.apache.commons.lang3.time.StopWatch;

import lombok.extern.log4j.Log4j2;

@Log4j2
@com.tempest.annotation.Task(name = "Transaction")
public class Transaction implements TransactionalTask {

    private Connection con = null;

    private List<Task> list;

    private boolean reRun = true;

    /**
     * コンストラクタ
     */
    public Transaction() {
        list = new ArrayList<>();
        try {
            this.createConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Transaction(boolean reRun) {
        this();
        this.reRun = reRun;
    }

    /**
     * ステーメントを作成します。
     *
     * @throws SQLException SQLの処理で例外が発生した場合。
     */
    private void createConnection() throws SQLException {
        con = ConnectionPool.getInstance().getConnection();
        con.setAutoCommit(false);
    }

    /**
     * 仕事をします
     *
     * @param share 共有Bean
     * @return true:仕事を続ける。 false:仕事を続けない
     * @throws InterruptedException 割り込みを受け取った場合
     */
    @Override
    public boolean exec(State share) throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug("exec start");
        }
        StopWatch watch = null;
        if (log.isInfoEnabled()) {
            watch = new StopWatch();
            watch.start();
        }
        try {
            if (this.con == null || (this.con != null && this.con.isClosed())) {
                this.createConnection();
            }
            for (Task task : this.list) {
                Field field = this.getField(task);
                field.setAccessible(true);
                field.set(task, this.con);
                task.exec(share);
            }
            this.commit();
            if (log.isInfoEnabled()) {
                watch.stop();
                log.info("DB access time:" + watch.getTime());
            }
        } catch (SQLException | IllegalArgumentException | IllegalAccessException e) {
            this.rollback();
            throw new RuntimeException(e);
        } finally {
            if (this.con != null) {
                try {
                    this.con.close();
                } catch (SQLException e) {
                }
            }
            this.con = null;
        }
        if (log.isDebugEnabled()) {
            log.debug("exec end");
        }
        return reRun;
    }

    /**
     * ロールバックする
     */
    @Override
    public void rollback() {
        try {
            if (log.isInfoEnabled()) {
                log.info("rollback.");
            }
            this.con.rollback();
            this.con.setAutoCommit(true);
        } catch (SQLException e) {
            log.warn("rollback.", e);
        }
    }

    /**
     * コミットする。
     */
    @Override
    public void commit() {
        try {
            if (log.isInfoEnabled()) {
                log.info("commit.");
            }
            this.con.commit();
            this.con.setAutoCommit(true);
        } catch (SQLException e) {
            log.warn("commit.", e);
        }
    }

    /**
     * タスク終了時に呼び出します。
     */
    @Override
    public void destroy() {
        this.list.stream().forEach(Task::destroy);
        this.list = null; // 持っているlistを開放する。
        this.rollback();
        if (this.con != null) {
            try {
                this.con.close();
            } catch (SQLException e) {
            }
        }
    }

    /**
     * 引数のタスク内にautowiredアノテーションがあるConnectionプロパティを見つけて返す。 存在しない場合はNullwo返す
     *
     * @param task タスク
     * @return タスクインスタンス内のConnectionプロパティを表すField、ない場合は、null
     */
    private Field getField(Task task) {
        Field[] fields = task.getClass().getDeclaredFields();
        log.info(fields.length);
        List<Field> list = Arrays.asList(fields).stream().filter(x -> {
            Autowired autowired = x.getDeclaredAnnotation(Autowired.class);
            return autowired != null;
        }).filter(x -> x.getType() == Connection.class).collect(Collectors.toList());
        if (list.size() > 0) {
            return list.get(0);
        }
        log.info("null");
        return null;
    }

    /**
     * 引数のタスクインスタンスにConnectionプロパティのあるないを確認する。
     *
     * @param task インスタンス
     * @return true:存在する、false:存在しない。
     */
    private boolean check(Task task) {
        return this.getField(task) != null;
    }

    /**
     *
     */
    public void add(Task task) {
        if (!this.check(task)) {
            throw new RuntimeException("Connectionプロパティが存在しません。");
        }
        list.add(task);
    }

    @Override
    public boolean execBefore() throws InterruptedException {
        return true;
    }

    public List<Task> getTasks() {
        return this.list;
    }
}
