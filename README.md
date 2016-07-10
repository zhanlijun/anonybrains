**spring boot是干什么用的？**
---
>spring boot是一个微框架，它充分利用了javaConfig的配置模式以及“约定优于配置”的方式,极大简化了基于spring mvc的web应用和rest应用开发。
>最近两年很火的一个概念：微服务架构，它倡导将复杂的应用拆分为离散的微小应用或者服务进行独立部署，而spring boot很契合这样的一个理念。

---
**如何使用spring boot进行web开发呢**
---
在公司内部已经有很多部门在使用spring boot进行生产应用的开发了，也取得了非常多的经验。AE团队已近积累了不少经验，我的学习例子基于已经建设好的内部资源。

spring boot安装使用环境要求：
<br/>
1. maven要求：maven3.0+以上才可以使用
2. jdk要求：jdk1.7或者以上
3. spring framework要求：spring4.0+以上版本

生成一个工程很简单，以前经常使用maven插件来生成，能用，但是比较复杂，而spring提供了 http://start.spring.io 来帮助生成一个spring boot工程，只需要在页面上选择好依赖的资源就可以自动生成，
alibaba内部AE的同事将SPRING INITIALIZR fork了下来，在ali内部可以使用这个 http://start.alibaba.net/ 来生成工程

<br/>
从一个最经典的helloworld开始，这里完成一个程序，运行后提供一个rest服务，访问http://localhost:8080/hello 时，返回‘hello world’的结果，
代码里没有其他程序，只有一个类：
<br/>

```java
@RestController
@SpringBootApplication
public class SpringbootHelloworldApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootHelloworldApplication.class, args);
    }
    
	@RequestMapping("/hello")
	public String helloworld(){
		return "hello world";
	}
}

```

直接运行mvn spring-boot:run或者在eclipse执行main方法，然后就可以访问http://localhost:8080/hello 可以看到页面返回了hello world

这段代码有很多有用的信息，这里大致做一些介绍：
1. SpringApplication是Spring Boot框架中描述Spring应用的类，它的run()方法会创建一个Spring应用上下文（Application Context）。另一方面它会扫描当前应用类路径上的依赖
2. Spring WebMvc框架会将Servlet容器里收到的HTTP请求根据路径分发给对应的@Controller类进行处理，@RestController是一类特殊的@Controller，它的返回值直接作为HTTP Response的Body部分返回给浏览器。
3. @RequestMapping注解表明该方法处理那些URL对应的HTTP请求，也就是我们常说的URL路由（routing)，请求的分发工作是有Spring完成的。例如上面的代码中http://localhost:8080/hello 路径就被路由至helloworld()方法进行处理
4. @SpringBootApplication 这个annotation是spring boot提供的一个简便的annotation，有些开发人员喜欢用 @Configuration, @EnableAutoConfiguration和 @ComponentScan这三个，其实他们是一个意思。

关于@SpringBootApplication的意义可以参考他的配置：
>Indicates a configuration class that declares one or more @Bean methods and also triggers auto-configuration and component scanning. 
>This is a convenience annotation that is equivalent to declaring @Configuration, @EnableAutoConfiguration and @ComponentScan.
>它声明了一个或者多个spring bean并且触发spring的自动配置和组件扫描

---
注：@RestController	和	@RequestMapping	注解是Spring	MVC注解（它们不是Spring	Boot的特定部分）。
>	@RequestMapping	注解提供路由信息。它告诉Spring任何来自"/"路径的HTTP请求都应该被映射到	helloworld	方 法。	
>	@RestController	注解告诉Spring以字符串的形式渲染结果，并直接返回给调用者。


**如何修改应用默认的配置？**
>比如这个hello应用，默认开放的端口是8080，如何修改这个端口？
>如何打开jmx端口？
>如何设置应用名称？
>如何设置日志配置路径？
>.......

从application.properties属性文件开始。

SpringApplication将从以下位置加载application.properties文件，并把它们添加到Spring	Environment中：
1.	当前目录下的一个/config子目录 
2.	当前目录 
3.	一个classpath下的/config包 
4.	classpath根路径（root）

这个列表是按优先级排序的（列表中位置高的将覆盖位置低的）。

如果不喜欢将application.properties作为配置文件名，你可以通过指定spring.config.name环境属性来切换其他的名称。你也 可以使用spring.config.location环境属性来引用一个明确的路径（目录位置或文件路径列表以逗号分割）。

```
$	java	-jar	myproject.jar	--spring.config.name=myproject 
//or
$	java	-jar	myproject.jar	--spring.config.location=classpath:/default.properties,classpath:/override.properties
```

这种方式在以后开发，测试，生成环境配置文件不同的时候就比较有用了

比如我们要修改应用名称、应用端口，jmx端口，只需要在application.properties中加入
```
server.port=7001
management.port=7002
```

就可以了。再次启动访问的时候会发现原来的8080端口不能使用了。

spring boot提供了很多的默认配置项，具体的可以参考 http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#common-application-properties 

内部有不少好的资料可以参考学习：http://www.atatech.org/articles/46848 
