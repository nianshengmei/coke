package pers.warren.ioc.kit;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.net.URI;

/**
 * java源代码文件对象
 *
 * @since 1.0.3
 */
public class CokeJavaFileObject extends SimpleJavaFileObject {

    /**
     * java源代码
     *
     * @since 1.0.3
     */
    private String contents = null;
    /**
     * 类名
     *
     * @since 1.0.3
     */
    private String className;

    public CokeJavaFileObject(String className, String contents) {
        super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.className = className;
        this.contents = contents;
    }

    /**
     * 获取java源代码
     *
     * @since 1.0.3
     */
    public CharSequence getCharContent(boolean ignoredEncodingErrors) throws IOException {
        return contents;
    }

    /**
     * 获取类名
     *
     * @since 1.0.3
     */
    public String getClassName() {
        return className;
    }
}
