package pers.warren.ioc.cel;

/**
 * 简单表达式项
 *
 * <p>非cel的表达式项</p>
 */
public class SimpleItem implements ExpressionItem {

    private String value;

    public SimpleItem(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
