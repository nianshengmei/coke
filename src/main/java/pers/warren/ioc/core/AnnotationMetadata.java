package pers.warren.ioc.core;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * 注解元数据信息
 * @author warren
 * @since jdk 1.8
 */
public class AnnotationMetadata {

    private Map<Class<?>,Annotation> annotationSet;

    private Class<?> clz;

    public AnnotationMetadata(Class<?> clz) {
        this.annotationSet = new HashMap<>();
        this.clz = clz;
    }

    public boolean hasAnnotation(Class<?> annotation){
        return annotationSet.containsKey(annotation);
    }

    public Annotation getAnnotation(Class annotation){
        return annotationSet.get(annotation);
    }

    public static AnnotationMetadata metadata(Class<?> clz){
        Annotation[] annotations = clz.getAnnotations();
        AnnotationMetadata annotationMetadata = new AnnotationMetadata(clz);
        for (Annotation annotation : annotations) {
            annotationMetadata.annotationSet.put(annotation.annotationType(),annotation);
        }
        return annotationMetadata;
    }

    public Class<?> getSourceClass(){
        return clz;
    }

}
