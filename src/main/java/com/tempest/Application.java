package com.tempest;

import java.util.function.Function;

import com.tempest.builder.ServiceManager;
import com.tempest.utils.FaildCreateObjectException;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

/**
 * Application
 */
public class Application {

    public Application() {
        ApplicationScope.getInstance();
    }

    private void registerService() {
        ServiceManager manager = ServiceManager.getInstance();
        String serviceAnnotation = "";
        try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo().scan()) {
            ClassInfoList classInfoList = scanResult.getClassesWithAnnotation(serviceAnnotation);
            manager.createService(classInfoList);
        }catch (FaildCreateObjectException e) {
            e.printStackTrace();
        }

    }

}
