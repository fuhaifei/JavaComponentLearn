package com.fhf.spring.tx.testcase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BookDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Integer getBookPriceByID(int id){
        return jdbcTemplate.queryForObject("select price from t_book where book_id=?", Integer.class, id);
    }
    public Integer updateStock(int num, int id){
        return jdbcTemplate.update("update t_book set stock = stock - ? where book_id = ?", num, id);
    }
}
