package com.fhf.spring.tx.testcase;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {

    @Autowired
    UserDao userDao;
    @Autowired
    BookDao bookDao;

    @Transactional
    public void buyBook(int userId, int bookId, int num) throws Exception {
        Integer price = bookDao.getBookPriceByID(bookId);
        if(price == null){
            throw new Exception("no this book");
        }
        int i = userDao.deduceBalanceById(userId, price, num);
        if(i != 1){
            throw new Exception("no such User");
        }
        bookDao.updateStock(num, bookId);
    }

    @Transactional
    public void testTx(boolean rollback) throws Exception {
        userDao.testTx(2);
        if(rollback){
            throw new ArithmeticException("BookService throw exception!");
        }
    }


}
