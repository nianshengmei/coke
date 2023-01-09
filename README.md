<p align="center">
	<a href="https://gitee.com/needcoke/coke"><img src="./image/newLogo.png" width="45%"></a>
</p>
<p align="center">
    <strong style="color: cyan">coke : a java ioc foundation framework </strong>
</p>

-------------------------------------------------------------------------------

## 📚简介
coke是一个类springboot的ioc框架，与之不同的是coke更为简单，纯粹。在使用方式上coke继承了springboot的简单易用，在框架本身上对开发者更加友好，是真正拥抱自定义框架的开发者的。

coke仅仅102KB大小，小型项目。由于其在预加载，容器后置处理上采用异步方案，使其相较于springboot项目拥有更快的启动速度。

coke拥有丰富的官方扩展包，[**🌎coke-extend项目**](https://gitee.com/needcoke/coke-extend)

-------------------------------------------------------------------------------

### 🎁coke适合哪些项目

- 1、对框架国产化有要求的项目
- 2、对包体积有要求的场景
- 3、高启动速度、低web延迟项目
- 4、轻量级项目或demo
- 5、java脚本

-------------------------------------------------------------------------------

## 📦安装

### 🍊Maven
在项目的pom.xml的dependencies中加入以下内容:

```xml

<dependency>
    <groupId>io.gitee.needcoke</groupId>
    <artifactId>coke</artifactId>
    <version>1.0.2-RELEASE</version>
</dependency>
```

### 🍐Gradle

```
implementation group: 'io.gitee.needcoke', name: 'coke', version: '1.0.2-RELEASE'
```

-------------------------------------------------------------------------------

## 📦使用文档

### 🚚1、启动类
[**🌎示例项目**](https://gitee.com/needcoke/coke-example/blob/master/example_run_demo/src/main/java/com/hello/coke/RunApplication.java)
使用coke框架必须要在启动类中按照如下格式书写,以RunApplication类为例:

```java
public class RunApplication {

    public static void main(String[] args) {
        CokeApplication.run(RunApplication.class, args);
    }
}
```

-------------------------------------------------------------------------------

### 🚚2、常用注解
[**🌎示例项目**](https://gitee.com/needcoke/coke-example/tree/master/example_run_demo)
-------------------------------------------------------------------------------
#### 🚗2.1 @Component

- a、@Component注解用于定义一个组件bean,该注解作用在类上。
- b、coke启动时会为@Component类创建一个单例bean。 
- c、通过@Component的value属性或者name属性可以修改bean在容器中的名称(唯一标识)，name属性的优先级高于value。
如果不为name和value赋值，则coke会为该bean赋默认名称，类名首字母首字母小写，该场景下(coke自动分配)如果名称与其他bean重复则会加上@符加六位随机字符。手动分配场景出现名称重复会直接报错。

<p>参考如下:</p>

```java
import pers.warren.ioc.annotation.Component;

//@Component(name = "userService")
//@Component("userService")
@Component
public class UserService {
    
    public void sayHello(){
        System.out.println("hello coke");
    }
}

```

-------------------------------------------------------------------------------

#### 🚗2.2 @Configuration
- a、@Configuration注解定义一个配置bean,该注解作用在类上。
- b、coke启动时会为@Configuration类创建一个单例bean。 
- c、通过@Configuration的value属性或者name属性可以修改bean在容器中的名称(唯一标识)，name属性的优先级高于value。
如果不为name和value赋值，则coke会为该bean赋默认名称，类名首字母首字母小写，该场景下(coke自动分配)如果名称与其他bean重复则会加上@符加六位随机字符。手动分配场景出现名称重复会直接报错。

<p>
    <a style="color: crimson">注:@Configuration所生成的bean在生成顺序上理论优先于@Component</a>
</p>

-------------------------------------------------------------------------------

#### 🚗2.3 @Value
@Value注解可以在bean中注入配置文件的配置。coke支持注入的配置文件包括: 启动命令行参数,环境变量,application.yaml,application.yml,application.properties,优先级从高到低。

<p>配置文件示例:</p>

```yaml
properties:
  demo: hello coke
  demo2: 233
```
<p>注入配置文件示例:</p>

```java
import pers.warren.ioc.annotation.Configuration;
import pers.warren.ioc.annotation.Value;

@Configuration
public class CokeConfig {

    @Value("properties.demo")
    private String demo;

    @Value("properties.demo2")
    private String demo2;

    @Value("properties.demo3:666")        //如果配置文件中找不到 properties.demo3对应的值，则赋予该字段默认值 666
    private String demo3;
}
```

#### 🚗2.4 @Bean

- a、@Bean用于定义一个简单bean，作用在方法上(该方法必须是@Component或@Configuration标注的类中的方法)
- b、coke启动时会为@Bean类创建一个单例bean。
- c、生成的bean的名称默认会使用@bean所标注的方法名,也可以通过@Bean的name属性手动分配。
coke自动分配名称，如果名称与其他bean重复则会加上@符加六位随机字符。手动分配场景出现名称重复会直接报错。

<p>示例如下:</p>

```java
import com.hello.coke.cp.User;
import pers.warren.ioc.annotation.Bean;
import pers.warren.ioc.annotation.Configuration;

@Configuration
public class CokeConfig {

    @Bean(name = "userDemo2")
    public User userDemo(){
        return new User();
    }
}
```

#### 🚗2.5 @Autowired

通过@Autowired可以在一个bean中注入另一个bean。该注解作用于属性，setter方法和构造函数上。


示例:

```java
import com.hello.coke.config.CokeConfig;
import pers.warren.ioc.annotation.Autowired;
import pers.warren.ioc.annotation.Component;

@Component
public class UserService {

    @Autowired("userDemo2")
    private User user;

    @Autowired
    private CokeConfig config;
    
    private Integer max;

    @Autowired
    public void setMax(Integer max) {
        this.max = max;
    }
    public void sayHello(){
        System.out.println(user.getName() + "hello" + hashCode());
        System.out.println(config.toString());
    }
}

```

### 🍉3、获取bean
- 1、通过@Autowired注入
- 2、通过@Resource注入
- 3、通过ApplicationContext.getBean(xxx)

```java
import com.hello.coke.cp.User;
import pers.warren.ioc.CokeApplication;
import pers.warren.ioc.annotation.Autowired;
import pers.warren.ioc.annotation.Component;
import pers.warren.ioc.core.ApplicationContext;

import javax.annotation.Resource;
import java.nio.file.attribute.UserDefinedFileAttributeView;

/**
 * 启动类
 */
@Component  //增加该注解，可以在启动类中注入需要使用的bean
public class RunApplication {

    @Autowired
    private static User user;

    @Resource
    private static User userDemo;

    public static void main(String[] args) {
        ApplicationContext applicationContext = CokeApplication.run(RunApplication.class, args);   //获取coke容器上下文
        User user1 = applicationContext.getBean("user");          //获取bean
        User user2 = applicationContext.getBean(User.class);      //获取bean
    }
}
```

### 4、详细文档
- [**1、♫更改banner打印**](https://juejin.cn/post/7185076457020850233)
- [**2、♫条件注入相关注解**](https://juejin.cn/post/7185076457020850233)  （待完善文档）
- [**3、♫web支持**](https://juejin.cn/post/7185076457020850233) （待完善文档）
- [**4、♫声明式web客户端**](https://juejin.cn/post/7185076457020850233) （待完善文档）
- [**5、♫web全局异常处理**](https://juejin.cn/post/7185076457020850233) （待完善文档）
- [**6、♫web拦截器**](https://juejin.cn/post/7185076457020850233) （待完善文档）
- [**7、♫AOP支持**](https://juejin.cn/post/7185076457020850233) （待完善文档）
- [**8、♫acids数据库开发框架**](https://juejin.cn/post/7185076457020850233) （待完善文档）
- [**9、♫coke自定义注解指南**](https://juejin.cn/post/7185076457020850233) （待完善文档）
## 📥下载jar

[**🌎下载页面**](https://gitee.com/needcoke/coke/releases)

## 🏗️添砖加瓦
coke开发者QQ群聊169208957,期待你的加入!
### 🧬贡献代码的步骤

- 1. 在Gitee或者Github上fork项目到自己的repo
- 2. 把fork过去的项目也就是你的项目clone到你的本地
- 3. 修改代码（记得一定要修改develop分支）
- 4. commit后push到自己的库（develop分支）
- 5. 登录Gitee或Github在你首页可以看到一个 pull request 按钮，点击它，填写一些说明信息，然后提交即可。
- 6. 等待维护者合并