package com.fhf.jdbc;

import java.sql.*;

public class TransactionTest {
    private static class AccountDao{
        public void add(String account, int money, Connection connection) throws SQLException {
            PreparedStatement s = connection.prepareStatement("update account set balance = balance + ? where name = ?");
            s.setInt(1, money);
            s.setString(2, account);
            s.executeUpdate();
            //关闭连接
            s.close();
        }

        public void deposit(String account, int money, Connection connection) throws SQLException {
            PreparedStatement s = connection.prepareStatement("update account set balance = balance - ? where name = ?");
            s.setInt(1, money);
            s.setString(2, account);
            s.executeUpdate();
            //关闭连接
            s.close();

        }
    }


    private static class AccountDaoV2{
        public void add(String account, int money) throws SQLException {
            PreparedStatement s = JDBCUtils.getConnection().
                    prepareStatement("update account set balance = balance + ? where name = ?");
            s.setInt(1, money);
            s.setString(2, account);
            s.executeUpdate();
            //关闭连接
            s.close();
        }

        public void deposit(String account, int money) throws SQLException {
            PreparedStatement s = JDBCUtils.getConnection().
                    prepareStatement("update account set balance = balance - ? where name = ?");
            s.setInt(1, money);
            s.setString(2, account);
            s.executeUpdate();
            //关闭连接
            s.close();
        }
    }


    public void transfer(String from, String to, int money) throws SQLException {
        Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/northwind?",
                "root", "fhf159357123");
        //关闭自动提交
        c.setAutoCommit(false);

        AccountDao accountDao = new AccountDao();
        try {
            //减去钱
            accountDao.deposit(from, money, c);
            //加上钱
            accountDao.add(to, money, c);
            //主动提交
            c.commit();
        }catch (SQLException e){
            //事务回顾
            c.rollback();
            throw e;
        }finally {
            //最终关闭连接
            c.close();
        }
    }

    public void transferv2(String from, String to, int money) throws SQLException {
        Connection c = JDBCUtils.getConnection();
        //关闭自动提交
        c.setAutoCommit(false);

        System.out.println(c.getAutoCommit() + " " +  c);

        AccountDaoV2 accountDao = new AccountDaoV2();
        try {
            //加上钱
            accountDao.add(to, money);
            //减去钱
            accountDao.deposit(from, money);
            //主动提交
            c.commit();
        }catch (SQLException e){
            //事务回滚
            c.rollback();
            throw e;
        }finally {
            //释放链接
            JDBCUtils.freeConnection();
        }
    }

    public static void main(String[] args) throws SQLException {
        new TransactionTest().transferv2("wang", "li", 1);
    }

}
