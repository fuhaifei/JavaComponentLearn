package com.fhf.jdbc;

import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于JDBCUtils，可以构建一个CRUD的基础父类
 * */
public class BaseDao {

    /**
     * DML操作
     * @param sql 包含占位符的sql语句
     * @param params 占位符对应参数
     * @return 受到影响的行数
     * */
    public int update(String sql, Object... params) throws SQLException {
        Connection connection = JDBCUtils.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
        int result = preparedStatement.executeUpdate();
        //若service层没有进行事务管理，需要释放连接
        if(connection.getAutoCommit()){
            JDBCUtils.freeConnection();
        }
        return result;
    }

    /**
     * DQL操作
     * @param clazz 返回对象的类文件
     * @param sql 查询字符串
     * @param params 占位符对应参数
     * */
    public <T> List<T> query(Class<T> clazz, String sql, Object... params) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        Connection connection = JDBCUtils.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        if(params != null){
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
        }
        ResultSet resultSet = preparedStatement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        //通过反射构建结果
        List<T> results = new ArrayList<>();
        while(resultSet.next()){
            T curRes = clazz.newInstance();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                Field f = curRes.getClass().getDeclaredField(metaData.getColumnLabel(i));
                f.setAccessible(true);
                f.set(curRes, resultSet.getObject(i));
            }
            results.add(curRes);
        }
        //若service层没有进行事务管理，需要释放连接
        if(connection.getAutoCommit()){
            JDBCUtils.freeConnection();
        }
        return results;
    }

    public static class Account{
        private String name;
        private Long balance;

        @Override
        public String toString() {
            return "Account{" +
                    "name='" + name + '\'' +
                    ", balance=" + balance +
                    '}';
        }
    }

    @Test
    public void TestUpdate() throws SQLException {
        update("insert into account values(?,?)", "niuma", 800);
    }

    @Test
    public void TestQuery() throws SQLException, NoSuchFieldException, InstantiationException, IllegalAccessException {
        System.out.println(query(Account.class,"select * from account where name = ?", "niuma"));
    }
}
