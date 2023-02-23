package com.fhf.spring.tx.testcase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    @Autowired
    UserDao userDao;

    @Autowired
    BookDao bookDao;

    @Transactional(propagation = Propagation.NESTED)
    public void testTx(boolean rollback) throws Exception {
        userDao.testTx(1);
        if(rollback){
            throw new ArithmeticException("UserService throw exception!");
        }
    }
}
