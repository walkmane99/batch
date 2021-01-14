package com.tempest.builder;

import com.tempest.utils.FaildCreateObjectException;
import com.tempest.utils.ReflectionUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tempest.function.LambdaExceptionUtil.*;

@EqualsAndHashCode(exclude = { "instance", "time", "isSingleton", "scope", "type" })
public class ObjectPreserve implements Comparable<ObjectPreserve>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compareTo(ObjectPreserve o) {
        return this.hashCode() - o.hashCode();
    }

    /**
     * インターフェースを考える。 イベントの発生？
     *
     */
    public enum BeanType {
        BEAN, COMPONENT, SERVICE;
    }

    public enum Scope {
        SYSTEM, APPLICATION
    }

    @Getter
    private boolean isSingleton;

    @Getter
    private Scope score;

    @Getter
    private Class<?> clazz;

    @Getter
    public String name;

    private transient Object instance;

    @Getter
    private BeanType type;

    @Getter
    private transient LocalDateTime time;
    @Getter
    private List<ObjectPreserve> preserveList;

    public ObjectPreserve(Class<?> clazz, String name, Scope scope, BeanType type) {
        this(clazz, name, scope, type, true);
    }

    public ObjectPreserve(Class<?> clazz, String name, Scope scope, BeanType type, boolean isSingleton) {
        preserveList = new ArrayList<>();
        this.clazz = clazz;
        this.name = name;
        this.score = scope;
        this.type = type;
        this.isSingleton = isSingleton;
    }

    public void addRelation(List<ObjectPreserve> list) {
        this.preserveList = list.stream().filter(preserve -> !preserve.equals(this))
                .filter(preserve -> necessary(preserve)).collect(Collectors.toList());
    }

    public Set<Class<?>> getAllExtendedOrImplementedTypesRecursively() {
        return ReflectionUtils.getAllExtendedOrImplementedTypesRecursively(this.clazz);
    }

    /**
     * インスタンスを作成する上で必要かまた、autowrideアノテーションで必要かどうか確認
     * 
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
                this.instance = newInstance();
                this.time = LocalDateTime.now();
            } else {
                return (T) newInstance();
            }
        }
        return (T) getInstance();
    }

    private Object newInstance() throws FaildCreateObjectException {
        try {
            // 必要なオブジェクトを先に作っておく。
            this.preserveList.stream().forEach(rethrowConsumer(ObjectPreserve::create));
            Object instance = ConstructorResolver.newInstance(this).orElseThrow();
            injectionAutowired(instance);
            return instance;
        } catch (Throwable throwable) {
            throw new FaildCreateObjectException("create faild.");
        }
    }

    Constructor<?>[] getConstractor() {
        return getClazz().getDeclaredConstructors();
    }

    public void injectionAutowired(Object target) {
        AutowiredResolver autowired = new AutowiredResolver();
        autowired.resolve(target);
    }

    public void disponse() {
        this.instance = null;
        this.time = null;
    }

    private <T> T getInstance() {
        return (T) this.instance;
    }

    public Object[] getObjects(Type[] types) throws FaildCreateObjectException {
        List<String> typeList = Stream.of(types).map(t -> t.getTypeName()).collect(Collectors.toList());
        return this.getPreserveList().stream().map(preserve -> {
            if (preserve.getAllExtendedOrImplementedTypesRecursively().stream()
                    .anyMatch(x -> typeList.contains(x.getTypeName()))) {
                return preserve;
            }
            return null;
        }).filter(Objects::nonNull).map(rethrowFunction(preserve -> preserve.create())).toArray();
    }
}
