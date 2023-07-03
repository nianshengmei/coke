package pers.warren.ioc.kit;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

/**
 * coke class文件对象
 *
 * <p>用于编译时候获取编译后的class文件</p>
 *
 * @author warren
 *
 * @since 1.0.3
 */

public class CokeJavaClassFileObject  extends SimpleJavaFileObject {

    /**
     * 输出流
     */
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    public CokeJavaClassFileObject(String name, JavaFileObject.Kind kind) {
        super(URI.create("string:///" + name.replace('.', '/') + kind.extension), kind);
    }
    /**
     * 从输出流中获取字节码信息
     *
     * @since 1.0.3
     */
    public byte[] getBytes() {
        return outputStream.toByteArray();
    }

    /**
     * 编译时候会调用openOutputStream获取输出流,并写数据
     *
     * @since 1.0.3
     */
    @Override
    public OutputStream openOutputStream() throws IOException {
        return outputStream;
    }

}
