package com.tempest;

import java.util.function.Function;

import com.tempest.builder.ObjectPreserve;
import com.tempest.builder.ServiceManager;
import com.tempest.utils.FaildCreateObjectException;
import com.tempest.utils.ReflectionUtils;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import static com.tempest.function.LambdaExceptionUtil.rethrowConsumer;

/**
 * Application
 */
public class Application {

    public Application() {
        ApplicationScope.getInstance();
    }

    private void registerService() throws FaildCreateObjectException {
        String serviceAnnotation = "";
        try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo().scan()) {
            ClassInfoList classInfoList = scanResult.getClassesWithAnnotation(serviceAnnotation);
            createObjectPreserves(classInfoList);
        }

    }

    private void createObjectPreserves(ClassInfoList list)  {
        list.stream().forEach(rethrowConsumer( classInfo -> createObjectPreserve(classInfo)));
    }

    private ObjectPreserve createObjectPreserve(ClassInfo classInfo) {
        ObjectPreserve obj =  new ObjectPreserve(classInfo.loadClass(), null,null);
        return obj;
    }


}
