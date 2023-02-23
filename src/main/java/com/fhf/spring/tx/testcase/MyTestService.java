package com.fhf.spring.tx.testcase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MyTestService {

    @Autowired
    UserService userService;

    @Autowired
    BookService bookService;
    @Autowired
    UserDao userDao;

    @Autowired
    BookDao bookDao;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void testTx(boolean rollback) {
        bookDao.updateStock(1, 3);
        System.out.println("call userService:");
        try{
            userService.testTx(true);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(rollback){
            throw new ArithmeticException("testService throw exception");
        }
    }
}
