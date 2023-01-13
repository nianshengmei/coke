package pers.warren.ioc.handler;

/**
 * Coke业务处理后置方法
 *
 * <p>对bean的写操作不应放在CokePostService中完成,应放在CokePostHandler中</p>
 * <p>CokePostService运行晚于CokePostHandler</p>
 * <p>
 * CokePostService必须先是一个bean才能生效，例如被@Component修饰
 * <p> 注意CokePostService是异步执行的,CokePostHandler是同步执行的
 * * @author warren
 */
public interface CokePostService {

    void run() throws Throwable;
}
