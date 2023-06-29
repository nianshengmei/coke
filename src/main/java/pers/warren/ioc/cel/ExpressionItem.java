package pers.warren.ioc.cel;

public interface ExpressionItem {

    static ExpressionItem getExpressionItem(String expressionString) {
        if(expressionString.startsWith("$(") && expressionString.endsWith(")")) {
            return new CELItem(expressionString);
        }
        return new SimpleItem(expressionString);
    }

    String getValue();
}
