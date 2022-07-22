package pers.warren.ioc.core;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * 注解元数据信息
 *
 * @author warren
 * @since jdk 1.8
 */
public class AnnotationMetadata {

    /**
     * 注解信息
     */
    private Map<Class<?>, Annotation> annotationSet;

    /**
     * 注解信息源类
     */
    private Class<?> clz;

    public AnnotationMetadata(Class<?> clz) {
        this.annotationSet = new HashMap<>();
        this.clz = clz;
    }

    /**
     * 判断特定注解是否存在
     */
    public boolean hasAnnotation(Class<?> annotation) {
        return annotationSet.containsKey(annotation);
    }

    /**
     * 获取特定注解
     */
    public Annotation getAnnotation(Class annotation) {
        return annotationSet.get(annotation);
    }

    /**
     * 构造注解原信息
     */
    public static AnnotationMetadata metadata(Class<?> clz) {
        Annotation[] annotations = clz.getAnnotations();
        AnnotationMetadata annotationMetadata = new AnnotationMetadata(clz);
        for (Annotation annotation : annotations) {
            annotationMetadata.annotationSet.put(annotation.annotationType(), annotation);
        }
        return annotationMetadata;
    }

    /**
     * 获取注解源类
     */
    public Class<?> getSourceClass() {
        return clz;
    }

}
