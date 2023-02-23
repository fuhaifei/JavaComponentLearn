package com.fhf.spring.tx;

import com.fhf.mybatis.entity.City;
import com.fhf.spring.tx.testcase.BookController;
import com.fhf.spring.tx.testcase.MyTestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * 声明式事务(基于@Tansactional注解)
 * 主要的属性有：
 * 1. propagation: 传播级别，指定当出现事务嵌套时的处理逻辑（重点在于嵌套，平行事务之间不存在任何关系）
 *    * REQUIRED: 存在事务加入事务，否则自己创建事务（A调用带有事务的B方法，A会加入B）
 *    * SUPPORTS: 存在事务加入事务，否则不开启事务
 *    * MANDATORY: 存在事务则加入，否则抛出异常
 *    * REQUIRES_NEW: 无论是否存在事务，均开启一个事务，挂起当前存在的事务
 *    * NOT_SUPPORTED: 暂停当前事务，以无事务状态运行
 *    * NEVER: 存在事务抛出一场，否则以无事务状态运行
 *    * NESTED: 如果当前事务存在，则在嵌套事务中执行，否则和REQUIRED操作相同（内部事务的回退会导致外部事务回退到savepoint,部分回滚）
 * 2. isolation：指定隔离级别，默认repeatable read(mysql)，与mysql的四个隔离级别已知
 * 3. readOnly：当前事务是只读
 * 4. timeout: 超时回滚
 * 5. rollback...: 指定回滚类
 * 可能会出现事务注解失效的情况：
 * 1. 注解在非public方法上。 Spring 通过反射获取@Transaction注解信息时，会检查方法作用域是否为public，若不为public，不解析@Transaction注解
 * 2. 方法内部调用时注解失效。 内部调用无法通过反射代替
 * 3. 抛出除RuntimeException和Error依赖的异常。By default, a transaction will be rolling back on RuntimeException and Error
 *    but not on checked exceptions (business exceptions). （见rollbackfor注释）
 * */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class TestTx {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    BookController bookController;

    @Autowired
    MyTestService testService;

    @Test
    public void testJdbc(){
        //查询
        RowMapper<City> cityRowMapper = new BeanPropertyRowMapper<>(City.class);
        //1.单个查询
        Integer city1 = jdbcTemplate.queryForObject("select count(*) from city where id = ? ", Integer.class, 100);
        System.out.println(city1);
        //2.批量查询批量查询
        List<City> henan = jdbcTemplate.query("select * from city where district = ?", cityRowMapper, "henan");
        for(City city:henan){
            System.out.print(city + ";");
        }
        System.out.println();

        //更新

        //1. 单个更新
        int update = jdbcTemplate.update("insert into city(name, countryCode,district, population) values(?,?,?,?)", "谷水西", "CHN", "henan", 100);
        System.out.println(update);
        //2. 批量更新
        String[] sql = new String[]{"insert into city(name, countryCode,district, population) values('谷水西', 'CHN', 'henan', 100);",
                "delete from city where name = '谷水西';"};
        int[] ints = jdbcTemplate.batchUpdate(sql);
        for(int i:ints){
            System.out.println(i);
        }

    }

    @Test
    public void testTx() throws Exception {
        bookController.buyBook(3, 3, 2);
    }

    @Test
    public void testParallelTX() {
        try {
            testService.testTx(false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
