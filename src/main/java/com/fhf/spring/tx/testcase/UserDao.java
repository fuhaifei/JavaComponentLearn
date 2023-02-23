package com.fhf.spring.tx.testcase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {
    @Autowired
    JdbcTemplate jdbcTemplate;


    public int deduceBalanceById(Integer userId, Integer price, Integer num){
        return jdbcTemplate.update("update t_user set balance = balance - ? where user_id = ?",
                price * num, userId);
    }

    public void testTx(int number){
        jdbcTemplate.update("update t_user set balance = balance + ?", number);
    }
}
