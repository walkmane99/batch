package com.tempest;

import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;

public class ByteTest {

    @Test
    public void test() throws Exception {
        ClassLoader clsldr = ByteTest.class.getClassLoader();

        // 単純なクラスの生成
        // https://bytebuddy.net/#/
        Class<?> dynamicType = new ByteBuddy().subclass(Object.class).method(ElementMatchers.named("toString"))
                .intercept(FixedValue.value("Hello World!")).make().load(clsldr).getLoaded();
        System.out.println(dynamicType.getConstructor().newInstance());

        // 単純なインターフェイスの生成
        // https://stackoverflow.com/questions/49139341/bytebuddy-create-interface-of-getter-setters
        // インターフェースの名前
        String className = "jp.seraphyware.example.MyInterfase";
        Builder<?> builder = new ByteBuddy().makeInterface().merge(Visibility.PUBLIC)
                .defineMethod("getMessage", String.class, Visibility.PUBLIC).withoutCode()
                .defineMethod("setMessage", void.class, Visibility.PUBLIC).withParameter(String.class).withoutCode()
                .defineMethod("showMessage", void.class, Visibility.PUBLIC).withoutCode().name(className);

        /*
         * 
         * 定義されたServieやコントローラクラスを解析、 そこからすべてのpublicメソッドを抽出。(親も含めて)
         * 抽出した情報から、インターフェースを動的に生成。 インターフェースから、Proxy 、InvocationHandlerを生成
         * 元クラスからインスタンスを作成。生成したプロキシに食わせる。 食わせたインスタンスを覚えておけば、AOPできると。
         * 
         * 
         * 
         */

        Class<?> intf = builder.make().load(clsldr).getLoaded();
        System.out.println(intf.getCanonicalName());
        for (Method m : intf.getMethods()) {
            System.out.println(m);
        }

        // 生成したクラスをクラスローダーから名前で引いてみる
        Class<?> intf2 = Class.forName(className, true, intf.getClassLoader());
        System.out.println(intf.equals(intf2));

        // DynamicProxyを使って生成したインターフェイスをハンドラに渡せるようにしてみる
        Object proxy = Proxy.newProxyInstance(intf.getClassLoader(), new Class<?>[] { intf }, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // この中に追加したい処理を書いていく感じ？
                System.out.println("call method: " + method);
                return null;
            }
        });

        Class<?> cls = proxy.getClass();
        cls.getMethod("getMessage").invoke(proxy);
        cls.getMethod("setMessage", String.class).invoke(proxy, "foo");
        cls.getMethod("showMessage").invoke(proxy);
        cls.getMethod("toString").invoke(proxy);
    }
}
