package pers.warren.ioc.kit;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

/**
 * coke文件管理器
 *
 * @since 1.0.3
 */
public class CokeFileManager extends ForwardingJavaFileManager {

    /**
     * coke类文件对象
     *
     * @since 1.0.3
     */
    private CokeJavaClassFileObject javaClassObject;

    protected CokeFileManager(StandardJavaFileManager standardJavaFileManager) {
        super(standardJavaFileManager);
    }

    /**
     * 获取coke类文件对象
     *
     * @since 1.0.3
     */
    public CokeJavaClassFileObject getJavaClassObject() {
        return javaClassObject;
    }

    /**
     * 获取coke类文件对象
     *
     * @since 1.0.3
     */
    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
        this.javaClassObject = new CokeJavaClassFileObject(className, kind);
        return javaClassObject;
    }

}
