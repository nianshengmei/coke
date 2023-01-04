package pers.warren.ioc.handler;

/**
 * coke容器的后置处理器
 *
 * <p>coke不保证在后置处理器中bean都加载完毕</p>
 * <p>因此对bean的写操作应该实现在该类的子类中，对bean的读操作应放在CokePostService中</p>
 *
 * CokePostHandler必须先是一个bean才能生效，例如被@Component修饰
 * @author warren
 */
public interface CokePostHandler {

    void run() throws Throwable;
}
