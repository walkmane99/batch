package com.tempest;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

/**
 * BeanLoader
 */
public class BeanLoader {

    public void registerService(String pkg, String annotation) {
        ServiceManager manager = ServiceManager.getInstance();
        // try (ScanResult scanResult = new ClassGraph().verbose() // Log to stderr
        try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo() // Scan classes, methods, fields,
                .scan()) {
            ClassInfoList classInfoList = scanResult.getClassesWithAnnotation(annotation);
            for (ClassInfo classInfo : classInfoList) {
                try {
                    // 作成できなくても続ける。
                    manager.createService(classInfo.loadClass());
                } catch (FaildCreateObjectException | InstantiationException | IllegalAccessException e) {
                }
            }
        }
        ;
    }

}
