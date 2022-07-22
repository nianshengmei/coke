# needcoke-ioc

想了解如何定义一款java ioc工具的具体流程吗，spring boot的源码太复杂看不懂？
needcoke-ioc的设计思想和spring /spring boot类似。你还可以基于needcoke-ioc开发 web支持，AOP,数据库支持，redis支持。了解Spring是如何与这些第三方工具对接的。


## 1、类spring boot的注解风格
@Component定义组件
@Configuration定义配置
@Scanner配置包扫描 ，必须与@Configuration一起使用
@Value配置文件注入
@Bean使用方法定义bean
@Autowired/@Resource注入bean

## 2、类 spring boot的 starter定义方式
### 2.1、扩展BeanRegister  参考pers.warren.ioc.core.DefaultBeanRegister
### 2.2、扩展BeanFactory  参考 pers.warren.ioc.core.DefaultBeanFactory 
### 2.3、扩展 FactoryBean 参考 pers.warren.ioc.core.DefaultFactoryBean / pers.warren.ioc.core.SimpleFactoryBean
### 2.4、在resource目录下扩展 /META-INF/coke.abc,大致内容如下

```abc
pers.warren.ioc.annotation.Configuration = {
    haha.vvb.xa.AConfiguration    //你定义的配置类
}
```

```java
package haha.vvb.xa;

import pers.warren.ioc.annotation.Configuration;
import pers.warren.ioc.annotation.Scanner;

@Scanner(value = {"org.spring"})
@Configuration(scanner = {"op.ik"})    //scanner属性和@Scanner作用相同
public class AConfiguration {

}

```

