package pers.warren.ioc.cel;

/**
 * coke表达式解析器
 *
 * <p>将表达式解析为Expression对象</p>
 *
 * @since 1.0.3
 */
public class CokeExpressionParser implements ExpressionParser {


    @Override
    public Expression parseExpression(String expressionString) {
       return new CokeExpression(expressionString);
    }
}
