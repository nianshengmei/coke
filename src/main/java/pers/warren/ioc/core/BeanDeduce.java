package pers.warren.ioc.core;

/**
 * bean推理
 * <p>
 * bean推理运行与所有后置拦截器之后，所有非构造器注入之前
 *
 * @author warren
 */
public interface BeanDeduce {

    void deduce();
}
