package pers.warren.ioc.kit;

import pers.warren.ioc.cel.CELClassLoader;

import javax.tools.*;
import java.util.ArrayList;
import java.util.List;

public class DynamicCompiler {
    /**
     * 编译出类
     *
     * @param fullClassName 全路径的类名
     * @param javaCode      java代码
     * @return 目标类
     */
    public Class<?> compileToClass(String fullClassName, String javaCode) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        CokeFileManager fileManager = new CokeFileManager(compiler.getStandardFileManager(diagnostics, null, null));

        List<JavaFileObject> jfiles = new ArrayList<>();
        jfiles.add(new CokeJavaFileObject(fullClassName, javaCode));

        List<String> options = new ArrayList<>();
        options.add("-encoding");
        options.add("UTF-8");


        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, jfiles);
        boolean success = task.call();
        if (success) {
            CokeJavaClassFileObject javaClassObject = fileManager.getJavaClassObject();
            CELClassLoader dynamicClassLoader = new CELClassLoader();
            //加载至内存
            return dynamicClassLoader.loadClass(fullClassName, javaClassObject);
        } else {
            for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
                String error = compileError(diagnostic);
                throw new RuntimeException(error);
            }
            throw new RuntimeException("compile error");
        }
    }

    private String compileError(Diagnostic diagnostic) {
        StringBuilder res = new StringBuilder();
        res.append("LineNumber:[").append(diagnostic.getLineNumber()).append("]\n");
        res.append("ColumnNumber:[").append(diagnostic.getColumnNumber()).append("]\n");
        res.append("Message:[").append(diagnostic.getMessage(null)).append("]\n");
        return res.toString();
    }
}