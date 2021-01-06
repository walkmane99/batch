package com.tempest.builder;

import com.tempest.utils.FaildCreateObjectException;
import com.tempest.utils.ReflectionUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@EqualsAndHashCode(exclude={"instance","time","isSingleton","scope","type"})
public class ObjectPreserve implements Comparable<ObjectPreserve>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compareTo(ObjectPreserve o) {
        return this.hashCode() - o.hashCode();
    }

    /**
     * インターフェースを考える。
     * イベントの発生？
     *
     */
    public enum BeanType {
        BEAN,COMPONENT,SERVICE;
    }
    public enum Scope {
        SYSTEM,APPLICATION
    }

    @Getter
    private boolean isSingleton;

    @Getter
    private Scope score;

    @Getter
    private Class<?> clazz;

    @Getter
    public String name;

    @Getter
    private transient Object instance;

    @Getter
    private BeanType type;

    @Getter
    private transient LocalDateTime time;

    private List<ObjectPreserve> preserveList;

    public ObjectPreserve(Class<?> clazz, String name, Scope scope, BeanType type ) {
        this(clazz, name,scope,type,true);
    }
    public ObjectPreserve(Class<?> clazz, String name, Scope scope, BeanType type, boolean isSingleton ) {
        preserveList = new ArrayList<>();
        this.clazz = clazz;
        this.name = name;
        this.score = scope;
        this.type = type;
        this.isSingleton = isSingleton;
    }

    public void addRelation(List<ObjectPreserve> list) {
        this.preserveList = list.stream()
            .filter(preserve-> !preserve.equals(this))
            .filter(preserve-> necessary(preserve))
            .collect(Collectors.toList());
    }

    /**
     * インスタンスを作成する上で必要かまた、autowrideアノテーションで必要かどうか確認
     * @param preserve
     * @return
     */
    private boolean necessary(ObjectPreserve preserve) {
        return true;
    }

    @SuppressWarnings("unchecked")
    public <T> T create() throws FaildCreateObjectException {
        if (isSingleton()) {
            if (getInstance() == null) {
                this.instance =newInstance();
                this.time = LocalDateTime.now();
            } else {
                return (T) newInstance();
            }
        }
        return (T)getInstance();
    }

    private Object newInstance()  throws FaildCreateObjectException {
        try {
            Object instance = ConstructorResolver.newInstance( ReflectionUtils.getConstructor(clazz)).orElseThrow();
            injectionAutowired(instance);
            return instance;
        } catch (Throwable throwable) {
            throw new FaildCreateObjectException("create faild.");
        }
    }




    public void injectionAutowired(Object target) {
        AutowiredResolver autowired = new AutowiredResolver();
        autowired.resolve(target);
    }

    public void disponse() {
        this.instance = null;
        this.time = null;
    }


}
