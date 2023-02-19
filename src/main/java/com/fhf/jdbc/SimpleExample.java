package com.fhf.jdbc;


import java.sql.*;
import java.util.*;

/**
 * JDBC一般使用流程的简单展示
 * */

public class SimpleExample {

    public static void standardProcedure() throws SQLException, ClassNotFoundException {

        //1.注册驱动（mysql-connector-java）
        //  * 驱动版本8.0以上需要导入com.mysql.cj.jdbc.Driver
        //  * 以下导入普通的com.mysql.jdbc.Driver
        //  * 现在已经会自动加载，不需要写该语句
        //  * 加载该类，并执行静态代码块中的代码，等价于注册驱动的代码
        //  * Class.forName("com.mysql.cj.jdbc.Driver");
        Class.forName("com.mysql.cj.jdbc.Driver");
        //
        //2.建立连接
        //  * url格式为 jdbc:数据库名://ip/数据库名?key=value&... -> jdbc:mysql://localhost:3306/northwind
        //  * 8.0.25之前版本驱动，需要手动添加时区参数:serverTimezone=Asia/Shanghai
        //  * 8版本以后默认使用utf-8编码，老版本(默认gbk)都需要添加:useUnicode=true&characterEncoding=UTF-8
        //  * 其他参数：userSSL=true/false
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/northwind", "root", "fhf159357123");

        //3.创建语句执行句柄
        Statement statement = connection.createStatement();

        //4.执行Sql语句获得结果

        // DQL，返回resultSet
        ResultSet rs = statement.executeQuery("select * from customers where first_name like 'A%'");
        while(rs.next()){
            System.out.println(rs.getInt("id") + " " + rs.getString("company") +
                    " " + rs.getString("last_name") + " " + rs.getString("first_name"));
        }

        // 非DQl,当操作语句为DML时，返回受到影响的行数；其他sql返回0
        statement.executeUpdate("update customers set first_name = 'haha' where last_name = 'Alexander'");
        //关闭连接
        rs.close();
        statement.close();
        connection.close();
    }

    public static int sqlInjection(String username) throws SQLException {
        //SQL 注入（injection）：通过构造特殊的sql语句，实现对于web应用非法操作

        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/northwind", "root", "fhf159357123");

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select count(*) from customers where first_name = '" + username + "'");
        resultSet.next();
        return resultSet.getInt(1);
    }


    public static int avoidInjection(Integer id) throws SQLException{
        //使用prepareStatement避免sql注入攻击
        //查询过程变为：1.构建sql语句，参数使用？替代
        //           2.设置动态参数，进行查询
        // 实际上就是先添加但单引号和后添加单引号的问题
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/northwind", "root", "fhf159357123");

        //3.创建语句执行句柄
        PreparedStatement statement = connection.prepareStatement("select count(*) from customers where id = ?");
        //setObject会根据传入对象类型，自动转化为Mysql对应数据类型
        statement.setObject(1,id);
        ResultSet resultSet = statement.executeQuery();
        System.out.println(statement);
        resultSet.next();

        //插入数据
        statement = connection.prepareStatement("insert into customers(first_name, last_name) values (?,?)");
        statement.setObject(1, "王");
        statement.setObject(2, "nb");
        if(statement.executeUpdate() == 1){
            System.out.println("插入成功");
        }else{
            System.out.println("插入失败");
        }

        //更新数据
        statement = connection.prepareStatement("update customers set last_name = ? where fisrt_name = ?");
        statement.setObject(1, "nb");
        statement.setObject(2, "彩笔");
        System.out.println("修改数据条数为："+statement.executeUpdate());

        //删除数据
        statement = connection.prepareStatement("delete from customers  where id = ?");
        statement.setObject(1, 1);
        System.out.println("修改数据条数为："+statement.executeUpdate());

        return resultSet.getInt(1);
    }

    public static List<Map<String, String>> getMetaData() throws SQLException {

        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/northwind", "root", "fhf159357123");

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select first_name, last_name, job_title from customers where id > 10");
        //metadata中存储包括列名，列数量，属性非空与否，表明等的信息
        ResultSetMetaData metaData = resultSet.getMetaData();
        //获取列信息
        List<Map<String, String>> result = new ArrayList<>();
        while(resultSet.next()){
            Map<String, String> curMap = new HashMap<>();
            for(int i = 1;i <= metaData.getColumnCount();i++){
                curMap.put(metaData.getColumnLabel(i), resultSet.getString(i));
            }
            result.add(curMap);
        }
        return result;
    }

    //主键回显
    public static void getPKReturn() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/northwind", "root", "fhf159357123");
        PreparedStatement preparedStatement = connection.prepareStatement("insert into customers(last_name, first_name, email_address) values(?, ? ,?)", Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, "铁柱");
        preparedStatement.setString(2, "王");
        preparedStatement.setString(3, "123456@qq.com");

        int i = preparedStatement.executeUpdate();
        if(i == 1){
            ResultSet pkRes = preparedStatement.getGeneratedKeys();
            pkRes.next();
            System.out.println("insert success, primary key is:"+pkRes.getInt(1));
        }else{
            System.out.println("insert failed");
        }

        preparedStatement.close();
        connection.close();
    }

    //批量插入
    public static void batchInsert() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/northwind?rewriteBatchedStatements=true", "root", "fhf159357123");
        PreparedStatement preparedStatement = connection.prepareStatement(
                "insert into customers(last_name, first_name, email_address) values(?, ? ,?)",
                Statement.RETURN_GENERATED_KEYS);
        long startTime = System.currentTimeMillis();
        for(int i = 0;i < 100;i++){
            preparedStatement.setString(1, "铁柱" + i);
            preparedStatement.setString(2, "王" + i);
            preparedStatement.setString(3, "123456@qq.com" + i);
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
        System.out.println(preparedStatement);
        System.out.println(System.currentTimeMillis() - startTime);
    }


    public static void main(String[] args) throws SQLException {
        batchInsert();
    }

}
