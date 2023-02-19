package com.fhf.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * 数据库连接池的基本使用
 * */
public class Druid {

    //代码中配置方式方式配置
    @Test
    public void codeConfig() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/northwind?rewriteBatchedStatements=true");
        dataSource.setUsername("root");
        dataSource.setPassword("fhf159357123");

        //设置连接输出
        dataSource.setInitialSize(10);
        //最大连接数量
        dataSource.setMaxActive(30);
        DruidPooledConnection connection = dataSource.getConnection();

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery("select name, balance from account limit 10");

        while(resultSet.next()){
            System.out.println(resultSet.getString(1) + " " + resultSet.getInt(2));
        }
        //释放
        connection.close();
    }
    @Test
    public void fileConfig() throws Exception {
        InputStream resourceAsStream = Druid.class.getClassLoader().getResourceAsStream("druid.properties");
        Properties properties = new Properties();
        properties.load(resourceAsStream);
        DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);

        Connection connection = dataSource.getConnection();

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery("select name, balance from account limit 10");

        while(resultSet.next()){
            System.out.println(resultSet.getString(1) + " " + resultSet.getInt(2));
        }
        //释放
        connection.close();
    }
}
