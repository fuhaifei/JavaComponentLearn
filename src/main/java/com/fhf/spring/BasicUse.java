package com.fhf.spring;

/**
 * Spring常见八股问题
 * 1. Spring Bean的生命周期
 *      * 整体上理解
 *          * 实例化 ：             createBeanInstance()
 *          * 属性赋值（依赖注入）：   populateBean()
 *          * 初始化：              initializeBean()
 *          * 销毁
 *      * 方法理解
 *          * 实例化bean，并完成依赖注入
 *          * Aware接口方法调调用（BeanNameAware,BeanFactoryAware,ApplicationContextAware）
 *          * BeanPostProcessor.postProcessBeforeInitialzation() 实现AOP
 *          * 调用初始化方法（ InitializingBean/init-method）
 *          * BeanPostProcessor.postProcessAfterInitialization（）
 * 2. Spring循环依赖问题
 *    * 循环依赖的场景：getter/setter依赖（Spring可以解决）、构造方法循环依赖（Spring无法解决）
 *    * 创建bean的过程：创建bean,初始化输入（依赖注入）,beanPostProcessor(),
 *    * 基于三级缓存解决循环依赖问题
 *      * 第一级缓存：存放完全初始化的对象
 *      * 第二级缓存：存储部分初始化的对象（避免访问到部分初始化的对象）
 *      * 第三级缓存：存放由ObjectFactory包装的AOP代理对象，在注入时生成代理对象，存到二级缓存
 *    * 二级缓存是为了区分完全创建和部分创建对象，避免获取对象时获取到未完全创建成功的对象
 *    * 三级缓存是为了满足Spring bean AOP生成代理对象时机的一致性，如果不使用三级缓存，会出现代理对象和非代理对象的冲突问题
 *          * ObjectFactory将代理对象的生成延迟到依赖注入时间点
 * 3. Spring 提供的事务机制
 *    * Spring提供了高层的事务抽象，定义了包括事务管理器（PlatformTransactionManager），事务定义（TransactionDefinition）和
 *      事务状态（TransactionStatus）三个接口实现对于数据库事务的抽象
 *    * 具体使用可以分为编程式事务和声明式事务，编程式事务需要手动编写控制事务的语句，声明式事务基于SpringAOP，通过在方法上添加@Transactional
 *      注解实现事务控制
 *    * Spring事务传播级别包括
 *      1. PROPAGATION_REQUIRED : 支持当前事务，如果不存在就新建一个
 *      2. PROPAGATION_SUPPORTS : 支持当前事务，如果不存在，就不使用事务
 *      3. PROPAGATION_MANDATORY : 支持当前事务，如果不存在，抛出异常
 *      4. PROPAGATION_REQUIRES_NEW : 如果有事务存在，挂起当前事务，创建一个新的事务
 *      5. PROPAGATION_NOT_SUPPORTED : 以非事务方式运行，如果有事务存在，挂起当前事务
 *      6. PROPAGATION_NEVER : 以非事务运行，如果有事务存在，抛出异常
 *      7. PROPAGATION_NESTED : 如果当前事务存在，则嵌套事务执行
 *          * 子事务不会独立提交，而是取决于父事务，当父事务提交，那么子事务才会随之提交；如果父事务回滚，那么子事务也回滚
 * 4. Spring BeanFactory 和 ApplicationContext的区别
 *      * ApplicationContext继承自BeanFactory,对BeanFactory进行了扩展（国际化，统一资源加载策略，容器内部事件发布）
 *      * ApplicationContext对于bean在初始化时完成全部加载，BeanFactory在调用getBean()时加载
 * */
public class BasicUse {
}
