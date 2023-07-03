package pers.warren.ioc.cel;

import java.util.ArrayList;
import java.util.List;

/**
 * 表达式
 *
 * @author warren
 * @since 1.0.3
 */
public class CokeExpression implements Expression {

    /**
     * 完整的表达式字符串
     */
    private String expressionString;

    /**
     * 表达式项
     *
     * <p>表达式项是表达式的最小单位</p>
     */
    private List<ExpressionItem> items;

    public CokeExpression(String expressionString) {
        this.expressionString = expressionString;
        boolean b = checkExpression(expressionString);
        if(!b){
            throw new RuntimeException("表达式格式错误");
        }
        items = getExpressionItems(expressionString);
    }

    @Override
    public <T> T getValue(Class<T> clazz) {
        StringBuilder builder = new StringBuilder();
        for (ExpressionItem item : items) {
            builder.append(item.getValue());
        }
        return (T)builder.toString();
    }

    /**
     * 检查字符串str中 是否$(出现 之后必然会有一个)出现
     *
     * @param str 需要被检查的字符串
     */
    private boolean checkExpression(String str){
        boolean flag = true;
        int notF_index = -1;
        boolean f = true;
        for (int i = 0; i < str.length(); i++) {
            if(flag && str.charAt(i) == '$' && i+1 < str.length() && str.charAt(i+1) == '('){
                notF_index = i+1;
                flag = false;
                continue;
            }
            if(!flag && str.charAt(i) == '$' && i+1 < str.length() && str.charAt(i+1) == '('){
                return false;
            }
            if(f && str.charAt(i) == ')'){
                flag = true;
                continue;
            }

            if(!f && str.charAt(i) == ')'){
                f = true;
                continue;
            }

            if (!flag && i != notF_index && str.charAt(i) == '(') {
                f = false;
            }
        }
        return flag;
    }


    /**
     * 从str中提取所有被$()包裹的字符串
     * 例如从'abc$(str::abc) $(env::sys.name)'中提取出['str::abc','env::sys.name']
     */
    public List<ExpressionItem> getExpressionItems(String str){
        List<ExpressionItem> list = new ArrayList<>();
        boolean flag = true;
        int notF_index = -1;
        int v = 0;
        int index = 0;
        boolean f = true;
        for (int i = 0; i < str.length(); i++) {
            if(flag && str.charAt(i) == '$' && i+1 < str.length() && str.charAt(i+1) == '('){
                notF_index = i+1;
                v = i;
                if(index< i) {
                    list.add(ExpressionItem.getExpressionItem(str.substring(index, i)));
                }
                flag = false;
                continue;
            }
            if(f && str.charAt(i) == ')'){
                flag = true;
                list.add(ExpressionItem.getExpressionItem(str.substring(v,i+1)));
                index = i+1;
                continue;
            }

            if(!f && str.charAt(i) == ')'){
                f = true;
                continue;
            }

            if (!flag && i != notF_index && str.charAt(i) == '(') {
                f = false;
            }
        }
        return list;
    }



}
