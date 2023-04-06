package com.fhf.spring;

/**
 * Spring常见八股问题
 * 1. Spring循环依赖问题
 * 2. Spring 提供的事务机制
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
 * */
public class BasicUse {
}
