package com.tempest.db.system;

import java.sql.Connection;
import java.sql.SQLException;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import lombok.extern.log4j.Log4j2;

//import static com.jfe.base.common.Constant.*;
/**
 * コネクションプールです。
 */
@Log4j2
public class ConnectionPool implements AutoCloseable {
    private static ConnectionPool instance;
    private DataSource datasource;
    private PoolProperties pool = new PoolProperties();
    private Config config;

    /**
     * コンストラクタ
     */
    private ConnectionPool() {
        this.config = ConfigFactory.load();
        String url = Environment.value(this.config.getString("db.url"));
        String className = Environment.value(this.config.getString("db.className"));
        String name = Environment.value(this.config.getString("db.name"));
        String password = Environment.value(this.config.getString("db.password"));
        log.info("url:" + url);
        log.info("className:" + className);
        log.info("name:" + name);
        log.info("password:" + password);
        this.connect(url, className, name, password);
    }

    /**
     * 保持している自分自身を返します
     *
     * @return ConnectionPool
     */
    public static ConnectionPool getInstance() {
        if (ConnectionPool.instance == null) {
            ConnectionPool.instance = new ConnectionPool();
        }
        return ConnectionPool.instance;
    }

    /**
     * DBと接続します。
     *
     * @param url       DB接続するURL
     * @param className ドライバ
     * @param name      ID(スキーマ)
     * @param password  パスワード
     */
    private void connect(String url, String className, String name, String password) {
        // 接続先の設定
        this.pool.setUrl(url);
        this.pool.setDriverClassName(className);
        this.pool.setUsername(name);
        this.pool.setPassword(password);
        // その他オプション
        // コネクションプールをConnectionPoolMBeanとして登録 初期値は true
        this.pool.setJmxEnabled(true);
        // 検査するアイドル接続に validationQuery を発行して DB との接続を確認するかどうか。 ようするに testOnBorrow,
        // testOnReturn のスレッド版。 true を指定した場合、実際に DB との通信が出来なくなっているオブジェクトを破棄する。
        // validationQuery が未指定の場合は何もしない。 この検査は、上記の minEvictableIdleTimeMillis
        // での検査後に行われる。 初期値は false 。
        this.pool.setTestWhileIdle(true);
        this.pool.setTestOnBorrow(true);
        this.pool.setTestOnReturn(true);
        // コネクションの死活を検証する際に使用するクエリを設定します。 nullもしくは空文字を指定した場合、検証は行われません。デフォルトはnullです。
        // DBによって切り替える
        // this.pool.setValidationQuery("SELECT 1 FROM SYSIBM.DUAL");
        this.pool.setValidationQuery("SELECT 1 ");
        // プールから取得されるコネクションがここに指定した間隔（ミリ秒）よりも長い間プールされていた場合、コネクションの死活を検証します。初期値は 10000
        this.pool.setValidationInterval(10000);
        // スレッドの実行間隔 (ミリ秒) を指定する。 1 未満だとスレッドは実行されない。 よって、その場合以下のパラメータ群は指定しても意味を持たない。
        // 初期値は -1 なので、デフォルトではこのスレッドは動作しない。
        this.pool.setTimeBetweenEvictionRunsMillis(30000);
        // プールに保持しておく最大のコネクション数 初期値は maxActive
        this.pool.setMaxActive(this.getMaxActive());
        // プールの起動時に作成されるコネクションの初期サイズ 初期値は10
        this.pool.setInitialSize(this.getInitialSize());
        // 利用可能なコネクションが存在しないときに待機する最大時間（ミリ秒単位） 初期値は 30000 = （30秒）
        this.pool.setMaxWait(10000);
        // アイドル接続がプール中に居座れる時間。（ミリ秒単位）接続がアイドル状態になってからこの時間を過ぎていると、切断し破棄する。 1
        // 未満は無制限を意味し、決して破棄されなくなる。 次の testWhileIdle の機能を利用するなら、無制限を指定するのも無意味ではない。 初期値は
        // 1800000 (30 分) 。
        this.pool.setMinEvictableIdleTimeMillis(1800000);
        // プールに保持する最小のコネクション数 初期値は initialSize
        this.pool.setMinIdle(this.getMinSize());
        // クローズ漏れを検知した際に、コネクションをクローズしていないアプリのスタックトレースをログに出力する場合に設定
        this.pool.setLogAbandoned(false);
        // クローズ漏れコネクションの検知を行う場合はtrueに設定
        this.pool.setRemoveAbandoned(true);
        // this.pool.setRemoveAbandoned(false);
        // クローズ漏れとみなすまでの時間 初期値は 60 sec
        this.pool.setRemoveAbandonedTimeout(60);
        this.pool.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        this.datasource = new DataSource();
        this.datasource.setPoolProperties(this.pool);

    }

    /**
     * コネクションプールの初期サイズを環境変数から取得します。 <br>
     * 定義がない場合、初期値は10
     *
     * @return コネクションプールの初期サイズ
     */
    private int getInitialSize() {
        if (this.config.hasPath("db.initial.size")) {
            return this.config.getInt("db.initial.size");
        }
        return 10;
    }

    /**
     * コネクションプールの最大サイズを環境変数から取得します。 <br>
     * 定義がない場合、初期値は100
     *
     * @return コネクションプールの初期サイズ
     */
    private int getMaxActive() {
        if (this.config.hasPath("db.max.active")) {
            return this.config.getInt("db.max.active");
        }
        return 100;
    }

    /**
     * コネクションプールの最小アイドルサイズを環境変数から取得します。 <br>
     * 定義がない場合、初期値は10
     *
     * @return コネクションプールの初期サイズ
     */
    private int getMinSize() {
        if (this.config.hasPath("db.min.size")) {
            return this.config.getInt("db.min.size");
        }
        return 10;
    }

    /**
     * データベースを閉じます。
     *
     */
    @Override
    public void close() {
        if (this.datasource != null) {
            this.datasource.close();
        }
    }

    /**
     * コネクションを返します。
     */
    public Connection getConnection() throws SQLException {
        try {
            int cnt = 0;
            while (cnt < 3) {
                Connection con = this.datasource.getConnection();
                if (!con.isClosed()) {
                    return con;
                }
            }
            throw new SQLException("DBコネクションが取得できませんでした");
        } catch (SQLException e) {
            log.error(e);
            throw e;
        }
    }
}
