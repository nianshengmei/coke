package pers.warren.ioc.cel;

public class SimpleItem implements ExpressionItem {

    private String value;

    public SimpleItem(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
