<p align="center">
	<a href="https://gitee.com/needcoke/coke"><img src="./image/logo.png" width="45%"></a>
</p>
<p align="center">
    <strong style="">coke : a java ioc foundation framework </strong>
</p>

-------------------------------------------------------------------------------

## 📚简介
coke是一个类springboot的ioc框架，与之不同的是coke更为简单，纯粹。在使用方式上coke继承了springboot的简单易用，在框架本身上对开发者更加友好，是真正拥抱自定义框架的开发者的。

coke仅仅102KB大小，小型项目。由于其在预加载，容器后置处理上采用异步方案，使其相较于springboot项目拥有更快的启动速度。

coke拥有丰富的官方扩展包，[**🌎coke-extend项目**](https://gitee.com/needcoke/coke-extend)

-------------------------------------------------------------------------------

### 🎁coke适合哪些项目

<p>1、对框架国产化有要求的项目</p>
<p>2、对包体积有要求的场景</p>
<p>3、高启动速度、低web延迟项目</p>
<p>4、轻量级项目或demo</p>
<p>5、java脚本</p>

-------------------------------------------------------------------------------

## 📦安装

### 🍊Maven
在项目的pom.xml的dependencies中加入以下内容:

```xml
        <dependency>
            <groupId>org.needcoke</groupId>
            <artifactId>coke</artifactId>
            <version>1.0.2</version>
        </dependency>
```

### 🍐Gradle

```
implementation 'org.needcoke:coke:1.0.2'
```

## 📦使用文档

### 1、启动类
[**🌎示例项目**](https://gitee.com/needcoke/coke-example/blob/master/example_run_demo/src/main/java/com/hello/coke/RunApplication.java)
使用coke框架必须要在启动类中按照如下格式书写,以RunApplication类为例:

```java
public class RunApplication {

    public static void main(String[] args) {
        CokeApplication.run(RunApplication.class, args);
    }
}
```

### 2、使用@Component定义类bean

