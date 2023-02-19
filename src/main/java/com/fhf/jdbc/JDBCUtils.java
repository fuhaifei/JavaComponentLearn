package com.fhf.jdbc;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 封装jdbc操作的静态类（线程公用版）
 * */
public class JDBCUtils {

    private static final DataSource dataSource;
    //线程局部变量，保证一个线程获取到同一个链接
    private static final ThreadLocal<Connection> threadCon;
    //静态代码块，只在加载类时创建一次对象
    static {
        //初始化dataSource
        InputStream resourceAsStream = Druid.class.getClassLoader().getResourceAsStream("druid.properties");
        Properties properties = new Properties();
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //初始化connection
        threadCon = new ThreadLocal<>();
    }

    public static Connection getConnection() throws SQLException {
        Connection c = threadCon.get();
        if(c == null){
            c = dataSource.getConnection();
            threadCon.set(c);
        }
        return c;
    }

    public static void freeConnection() throws SQLException {
        Connection c = threadCon.get();
        if(c != null){

            //不需要手动重置，连结回收时会自动重置相关属性(holder.reset())
//            if (this.underlyingAutoCommit != this.defaultAutoCommit) {
//                this.conn.setAutoCommit(this.defaultAutoCommit);
//                this.underlyingAutoCommit = this.defaultAutoCommit;
//            }
            c.setAutoCommit(true);
            c.close();
            threadCon.remove();
        }
    }
}
