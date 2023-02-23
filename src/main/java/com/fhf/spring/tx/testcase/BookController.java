package com.fhf.spring.tx.testcase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class BookController {
    @Autowired
    BookService bookService;
    public void buyBook(int userId, int bookId, int num) throws Exception {
        bookService.buyBook(userId, bookId, num);
    }
}
