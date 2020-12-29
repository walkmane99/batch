package com.tempest.sql.system;

/**
 * Environment
 * 環境変数から取得する処理を行います。
 */
public class Environment {
    /**
     * プロセスNO
     */
    public static final String PROC_NO = "PROC_NO";
    /**
     * ユニークID
     */
    public static final String UNIQUE_ID = "UNIQUE_ID";

    // DB
    public static final String DB_URL = "DB_URL";
    public static final String DB_USERNAME = "DB_URL";
    public static final String DB_PASSWORD = "DB_USERNAME";
    // デバックモード
    public static final String DEBUG_MODE ="DEBUG_MODE";

    public static final String KUBERNETES ="KUBERNETES_SERVICE_HOST";

    public static final String REPOSITORY = "DOCKER_REPOSITORY";
    public static final String APIKEY = "KUBERNETES_APIKEY";

    /**
     * MOM3って書いてあるけどMON1もつかってるよ。
     */
    public static final String MODEL = "MON3_ANA_EXE_MODEL_FOLDER";


    public static boolean isKubernetes() {
        return Environment.get(KUBERNETES) == null? false:true;
    }

    public static String getProcNo() {
        return get(PROC_NO);
    }
    public static String getUniqueId() {
        return get(UNIQUE_ID);
    }

    public static String getRepository() {
        return get(REPOSITORY);
    }

    public static String getApiKey() {
        return get(APIKEY);
    }

    public static String getModelDirectory() {
        return get(MODEL);
    }

    public static String get(String name){
        return System.getenv(name);
    }
    /**
     * 環境変数からの取得を試す。
     * 下記フォーマットの文字列が与えられた場合、環境変数の取得を試み、取得した値を返します。
     * ${<環境変数>:初期値}
     *
     * 環境変数の値が取得できなかった場合、初期値の値を返します。
     * また、上記フォーマットに従っていない場合、引数をそのまま返します。
     *
     * @param value　${<環境変数>:初期値}のフォーマット
     * @return 環境変数から取得した値、または、初期値
     */
    public static String value(String value) {
        if (!value.startsWith("$")) {
            return value;
        }
        String tmpValue = null;
        String[] elements = value.split(":", 2);
        tmpValue = get(elements[0].substring(2));
        if (tmpValue != null)  {
            return tmpValue;
        }
        return elements[1].substring(0, elements[1].length() -1);
    }

}
