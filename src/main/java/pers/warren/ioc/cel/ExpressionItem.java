package pers.warren.ioc.cel;

/**
 *
 * CEL解析表达式项接口
 *
 * @author warren
 * @since 1.0.3
 */
public interface ExpressionItem {

    /**
     * 将表达式string转为表达式项
     *
     * @since 1.0.3
     */
    static ExpressionItem getExpressionItem(String expressionString) {
        if(expressionString.startsWith("$(") && expressionString.endsWith(")")) {
            return new CELItem(expressionString);
        }
        return new SimpleItem(expressionString);
    }

    /**
     * 获取表达式项对应的值
     *
     * @since 1.0.3
     */
    String getValue();
}
