package pers.warren.ioc.cel;


import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import pers.warren.ioc.core.BeanDefinition;
import pers.warren.ioc.core.Container;
import pers.warren.ioc.kit.JavaCompilerKit;

import java.lang.reflect.Method;

/**
 * CEL表达式项
 *
 * @author warren
 * @since 1.0.3
 */
public class CELItem implements ExpressionItem {

    /**
     * cel item的mode是啥
     *
     * @since 1.0.3
     */
    @Getter
    private String mode;

    private String beanName;

    private String envKey;

    private String javaExpression;

    private String value;

    private String methodName;

    private String fieldName;

    private final String expressionString;

    private static final String STR_MODE = "$(str::";

    private static final String INT_MODE = "$(int::";

    private static final String F_MODE = "$(f::";

    private static final String BEAN_MODE = "$(bean";

    private static final String BEAN_MODE_M = "$(bean-m::";

    private static final String BEAN_MODE_F = "$(bean-f::";

    private static final String BEAN_MODE_E = "$(bean::";

    private static final String ENV_MODE = "$(env::";

    private static final String JAVA_MODE = "$(j::";

    public CELItem(String expressionString) {
        this.expressionString = expressionString;
        if (expressionString.startsWith(STR_MODE)) {
            mode = STR_MODE;
            javaExpression = expressionString.substring(STR_MODE.length(), expressionString.length() - 1);
            value = javaExpression;
        } else if (expressionString.startsWith(INT_MODE)) {
            mode = INT_MODE;
            javaExpression = expressionString.substring(INT_MODE.length(), expressionString.length() - 1);
            value = javaExpression;
        } else if (expressionString.startsWith(BEAN_MODE)) {
            mode = BEAN_MODE;
            handleBeanMode(expressionString);
        } else if (expressionString.startsWith(ENV_MODE)) {
            mode = ENV_MODE;
            envKey = expressionString.substring(ENV_MODE.length(), expressionString.length() - 1);
        } else if (expressionString.startsWith(JAVA_MODE)) {
            mode = JAVA_MODE;
            javaExpression = expressionString.substring(JAVA_MODE.length(), expressionString.length() - 1);
        } else if (expressionString.startsWith(F_MODE)) {
            mode = F_MODE;
            javaExpression = expressionString.substring(F_MODE.length(), expressionString.length() - 1);
            value = javaExpression;
        } else {
            throw new RuntimeException("不支持的表达式:" + expressionString);
        }
    }

    public String getValue() {
        switch (mode) {
            case STR_MODE:
            case INT_MODE:
                return value;
            case BEAN_MODE_E:
                return JavaCompilerKit.runBeanString(javaExpression,beanName).toString();
                case BEAN_MODE_M:
                    try {
                        Object bean = Container.getContainer().getBean(beanName);
                        BeanDefinition beanDefinition = Container.getContainer().getBeanDefinition(beanName);
                        Method method = ReflectUtil.getMethod(beanDefinition.getClz(),methodName);
                        method.setAccessible(true);
                        Object invoke = method.invoke(bean);
                        return null != invoke?invoke.toString():null;
                    }catch (Throwable e){
                        throw new RuntimeException(String.format("bean:%s中没有找到方法:%s,或没有该bean",beanName,methodName),e);
                    }
                return JavaCompilerKit.runBeanString(javaExpression, beanName).toString();
            case BEAN_MODE_M:
                try {
                    Object bean = Container.getContainer().getBean(beanName);
                    BeanDefinition beanDefinition = Container.getContainer().getBeanDefinition(beanName);
                    Method method = beanDefinition.getClz().getMethod(methodName);
                    method.setAccessible(true);
                    return method.invoke(bean).toString();
                } catch (Throwable e) {
                    throw new RuntimeException(String.format("bean:%s中没有找到方法:%s,或没有该bean", beanName, methodName), e);
                }

            case BEAN_MODE_F:
                Object bean = Container.getContainer().getBean(beanName);
                return ReflectUtil.getFieldValue(bean, fieldName).toString();
                Object fieldValue = ReflectUtil.getFieldValue(bean, fieldName);
                return null!=fieldValue?fieldValue.toString():null;
            case ENV_MODE:
                return (String) Container.getContainer().getProperty(envKey);
            case JAVA_MODE:
                String tmpJavaExpression = javaExpression.replace("'", "\"");
                return String.valueOf(JavaCompilerKit.compile(tmpJavaExpression));
        }
        return value;
    }

    /**
     * 处理bean模式
     */
    private void handleBeanMode(String expressionString) {
        if (StrUtil.count(expressionString, '.') < 1) {
            throw new RuntimeException("不支持的表达式:" + expressionString);
        }
        if (expressionString.startsWith("$(bean-m::")) {
            beanName = expressionString.substring("$(bean-m::".length(), expressionString.indexOf('.'));
            methodName = expressionString.substring(expressionString.indexOf('.') + 1, expressionString.length() - 1);
            mode = BEAN_MODE_M;
        } else if (expressionString.startsWith("$(bean-f::")) {
            beanName = expressionString.substring("$(bean-f::".length(), expressionString.indexOf('.'));
            fieldName = expressionString.substring(expressionString.indexOf('.') + 1, expressionString.length() - 1);
            mode = BEAN_MODE_F;
        } else if (expressionString.startsWith("$(bean::")) {
            beanName = expressionString.substring("$(bean::".length(), expressionString.indexOf('.'));
            javaExpression = expressionString.substring(expressionString.indexOf('.') + 1, expressionString.length() - 1);
            mode = BEAN_MODE_E;
        } else {
            throw new RuntimeException("不支持的表达式:" + expressionString);
        }
    }

    private String beanModeMethod() {
        return null;
    }

}
