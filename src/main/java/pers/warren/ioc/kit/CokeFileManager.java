package pers.warren.ioc.kit;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

public class CokeFileManager extends ForwardingJavaFileManager {

    private CokeJavaClassFileObject javaClassObject;

    protected CokeFileManager(StandardJavaFileManager standardJavaFileManager) {
        super(standardJavaFileManager);
    }

    public CokeJavaClassFileObject getJavaClassObject() {
        return javaClassObject;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
        this.javaClassObject = new CokeJavaClassFileObject(className, kind);
        return javaClassObject;
    }

}
