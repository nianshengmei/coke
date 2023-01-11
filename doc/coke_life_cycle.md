## 1、coke生命周期时序图

<img src="https://gitee.com/needcoke/coke/raw/develop_warren/image/coke_life_cycle.png" width="100%">


## coke生命周期

- 1、start
- 2、包扫描
- 3、配置文件扫描
- 4、预加载
- 5、初始化BeanDefinition
- 6、BeanPostProcessor前置方法执行
- 7、BeanPostProcessor前置方法的后置方法执行
- 8、bean生成
- 9、BeanPostProcessor后置方法执行
- 10、bean推断
- 11、bean注入
- 12、配置文件注入
- 13、@init方法执行
- 14、Coke启动后置方法
- 15、Coke异步后置业务方法执行
- 到这容器已经启动完成!