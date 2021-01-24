package com.tempest;

import java.util.function.Function;

import com.tempest.annotation.Bean;
import com.tempest.annotation.Component;
import com.tempest.annotation.Service;
import com.tempest.builder.BeanBuilder;
import com.tempest.builder.ObjectPreserve;
import com.tempest.builder.ObjectPreserveList;
import com.tempest.store.Store;
import com.tempest.utils.FaildCreateObjectException;
import com.tempest.utils.ReflectionUtils;
import io.github.classgraph.*;

import static com.tempest.function.LambdaExceptionUtil.rethrowConsumer;

/**
 * Application
 */
public class Application {

    public static void main(String... args) {
        Application app = new Application();
        try {
            app.registerService();
        } catch (FaildCreateObjectException e) {
            e.printStackTrace();
        }
    }

    public Application() {
        BeanBuilder.getInstance();
        ApplicationScope.getInstance();
    }

    void registerService() throws FaildCreateObjectException {

        try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo().scan()) {
            ClassInfoList componentInfoList = scanResult.getClassesWithAnnotation(Component.class.getName());
            ClassInfoList serviceInfoList = scanResult.getClassesWithAnnotation(Service.class.getName());
            createObjectPreserves(componentInfoList, ObjectPreserve.BeanType.COMPONENT);
            createObjectPreserves(serviceInfoList, ObjectPreserve.BeanType.SERVICE);
        }

    }

    private void createObjectPreserves(ClassInfoList list, ObjectPreserve.BeanType type) {
        Store store = (Store) Store.getInstance();
        ObjectPreserveList preserveList = this.getPreserveList();
        list.stream().forEach(rethrowConsumer(classInfo -> preserveList.add(createObjectPreserve(classInfo, type))));
        store.put(preserveList.getClass().getName(), preserveList);
    }

    private ObjectPreserveList getPreserveList() {
        ObjectPreserveList preserveList = (ObjectPreserveList) Store.getInstance()
                .getProperties(ObjectPreserveList.class).orElseGet(ObjectPreserveList::new);
        return preserveList;
    }

    private ObjectPreserve createObjectPreserve(ClassInfo classInfo, ObjectPreserve.BeanType type) {
        String annotationName = getAnnotationName(type);
        AnnotationInfo info = classInfo.getAnnotationInfo(annotationName);
        String name = (String) info.getParameterValues().stream().filter(value -> value.getName().equals("name"))
                .map(value -> value.getValue()).findFirst().orElse(null);
        ObjectPreserve obj = new ObjectPreserve(classInfo.loadClass(), name, null, type);
        return obj;
    }

    private String getAnnotationName(ObjectPreserve.BeanType type) {
        if (type == ObjectPreserve.BeanType.BEAN) {
            return Bean.class.getName();
        } else if (type == ObjectPreserve.BeanType.SERVICE) {
            return Service.class.getName();
        } else if (type == ObjectPreserve.BeanType.COMPONENT) {
            return Component.class.getName();
        }
        return Service.class.getName();
    }

}
