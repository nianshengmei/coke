package pers.warren.ioc.cel;

public class CokeExpressionParser implements ExpressionParser {


    @Override
    public Expression parseExpression(String expressionString) {
       return new CokeExpression(expressionString);
    }
}
