package pers.warren.ioc.cel;

/**
 * coke 表达式增强处理
 *
 * @author warren
 * @since 1.0.3
 */
public interface ExpressionParser {

    /**
     * coke Expression Language
     *
     * <p>$(str::abc)</p> 字符串abc  $(str::)$(str::1+2+3+4+00H$)前面不输出输出后面
     * <p>$(int::1) 数字1</p>
     * <p>$(f::1.2) 小数1.2</p>精度有问题1.2+2.4=3.5999999999999996 不支持的表达式:$(f::1.2)
     * <p>$(env::service.name)  从环境中取 key为service.name的值</p>  $(env::)service.name)输出nullnull
     * <p>$(bean-m::user.getUserName)   从容器中取名为user的bean的getUserName()方法的返回值</p>
     * <p>$(bean-f::user.name) 从容器中取名为user的bean的name字段的值</p>
     * <p>$(j:System.currentTimeMillis()) 获取当前时间戳</p>
     *
     * @since  1.0.3 、 1.0.4
     * <p>
     * >>箭头表达式支持 json,toString两种箭头输出
     * <p>$(bean-m::user.getUserName#p1:S001>>json)</p>   //支持以p的形式传参，p1:S001表示第一个参数为S001，>>json表示返回值转为json
     * $(bean-f::user.name>>json)  //字段值转为json
     * <p>
     * <<箭头表达式支持 json,toString输入到bean中
     * <p>
     * $(bean-m::user.save<<{"name":"abc"})
     */
    Expression parseExpression(String expressionString);
}
