package com.fhf.spring.IOC;

import com.fhf.spring.IOC.annotation.TestController;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestIOC {
    static ApplicationContext ap;
    @BeforeClass
    public static void init(){
        //包含三个子类：ClassPathXmlApplicationContext,FileSystemXmlApplicationContext,ConfigurableApplicationContext
        ap = new ClassPathXmlApplicationContext("applicationContext.xml");
    }

    @Test
    public void helloSpringCase(){
        //通过id+type形式获取
        HelloSpring hs1 = ap.getBean("helloSpring", HelloSpring.class);
        //通过id获取
        HelloSpring hs2 = (HelloSpring)ap.getBean("helloSpring");
        //通过类型获取
        HelloSpring hs3 = ap.getBean(HelloSpring.class);
        //三个对象完全相同
        System.out.println(hs1 + " " + hs2 + " " + hs3);
    }

    @Test
    public void testExtends(){
        //子类bean与父类bean冲突，无法以类形式导入
//        FatherBean b1 = ap.getBean(FatherBean.class);
//        b1.introduce();
        //通过id访问不会出现bean冲突问题
        FatherBean b2 = (FatherBean) ap.getBean("childBean");
        b2.introduce();
        //接口同样可以实现自动装配
        Comparable b3 = ap.getBean(Comparable.class);
        System.out.println(b3);
    }

    @Test
    public void testIOC(){
        Bicycle bicycle = (Bicycle)ap.getBean("bicycle");
        System.out.println(bicycle);
    }

    @Test
    public void injectConfigFile() throws SQLException {
        DataSource ds = (DataSource)ap.getBean("druidDataSource");
        Connection connection = ds.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("select last_name from customers;");
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            System.out.println(resultSet.getString(1));
        }
    }

    @Test
    public void testAnnotation(){
        TestController bean = ap.getBean("testController",TestController.class);
        bean.testController();
    }
}
