package com.fhf.javaweb;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *  Cookies：客户端用来维护通信状态的数据
 *     * 由服务端设置，响应中携带Set-Cookie表明设置Cookie
 *     * 例如：Set-Cookie: region=shenyang; Path=/
 *     * JSESSIONID为服务端设置的用来匹配session的cookie
 *
 * */

@WebServlet(urlPatterns = "/cookie")
public class CookieServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //输出请求中携带的cookie
        Cookie[] cookies = req.getCookies();
        for(Cookie cookie:cookies){
            System.out.println(cookie.getName() + " " + cookie.getValue());
        }

        //设置cookie
        Cookie cookie = new Cookie(req.getParameter("cookieName"), req.getParameter("cookieValue"));
        cookie.setComment("测试cookie");
        //设置cookie使用的url范围
        cookie.setPath("/");
        resp.addCookie(cookie);
//
//        PrintWriter writer = resp.getWriter();
//        writer.write("<html><body><h1>cookie test request</h1></body></html>");
//        resp.setContentType("Content-Type:text/html;charset=utf-8");
//        writer.flush();
    }


}
