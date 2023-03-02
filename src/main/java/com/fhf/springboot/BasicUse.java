package com.fhf.springboot;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * SpringBoot学习，相关知识点简单总结
 * 常见注解总结
 * 1.@Import(xx.class,xx.class) 等价于spring.xml中用来导入其他配置文件的<import>标签，通常用来导入配置类
 *   * @ImportResource(xx.xml) 导入配置文件
 * 2.@Conditional() 条件装配系列注解，当满足一定条件时才执行该注解标注的配置类（类上）/java bean（方法上）的注入
 * 3.@ConfigurationProperties(prefix="") 将application.yml中指定前缀的配置加载到当前javaBean中
 *  * @EnableConfigurationProperties(xx.class)或者@Component使得对应类成为javaBean
 * 4.@SpringBootApplication的底层原理
 *  * @SpringBootConfiguration 继承自@Configuration注解，表示为SpringBoot的配置类
 *  * @EnableAutoConfiguration 实现自动配置的注解，包括两部分内容
 *     * @Import(AutoConfigurationImportSelector.class) -> (容器启动后，调用ImportSelector)selectImports()-> getAutoConfigurationEntry() -> getCandidateConfigurations()
 *       -> 读取 META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports 文件（Spring 2.7.4）
 *          老版本：读取 META-INF/spring.factories 文件
 *     * @AutoConfigurationPackage 导入一个Register组件，实现将主程序类所在包及所有子包下的组件到扫描到spring容器中
 * 5. SPI(Service Provider Interface) 制定服务的接口，不同服务的提供商提供不同的实现方案的同时遵循相同的服务接口。
 *  * JAVA SPI：通过java.util.ServiceLoader类解析classPath和jar包的META-INF/services/目录下的以接口全限定名命名的文件
 *              并加载该文件中指定的接口实现类，其中实现类可以有多个（文件名为接口全类名，文件内容为指定的接口实现类）
 *  * Spring SPI：类似于JAVA SPI，在spring.factories文件中配置接口->实现类的映射关系，通过扫描该文件自动装配对应接口的实现类
 *  * 区别：Spring SPI一个文件配置多个接口和实现类的对应关系，Java SPI一个文件对应一个接口和其是实现类
 * 基本知识点总结：
 * 1. 针对不同的环境（生产，部署），SpringBoot提供配置文件的切换功能
 *    * spring.profiles.active: production 指定当前激活的配置文件
 *    * 基于@Profiles()注解实现配置环境的条件装配
 *    * 多配置文件可以配置文件段的形式实现(spring.config.activate.on-profile:xxx),也可以多配置文件的形式实现(application-{xxx}.yml)
 * */
@Configuration
public class BasicUse {

}
