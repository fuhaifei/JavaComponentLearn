package com.fhf.javaweb;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Session和Cookie机制实现有状态通信
 * 1. 客户端访问第一次访问服务器后会分配一个sessionID,服务器维护一个sessionID-》session的映射
 * 2. 客户端通过名为JSESSIONID的cookies存储sessionID（该cookie由Servlet容器自动创建）
 * 3. 使用Session能够实现有状态通信，但是在分布式环境中session无法跨越多台机器,解决方案：
 *     * 服务器端session同步
 *     * 反向代理根据session将请求路由到一个服务器上
 * 4. session和cookies的作用类似，只是存储位置不同
 * */

@WebServlet("/session")
public class SessionServlet extends HttpServlet {
    private static Map<String, String> userDatabase;


    @Override
    public void init() throws ServletException {
        super.init();
        userDatabase = new HashMap<>();
        userDatabase.put("牛马一号", "1");
        userDatabase.put("牛马二号", "2");
        userDatabase.put("牛马三号", "3");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //返回登录页面

        //输出登录页面
        if(req.getSession().getAttribute("username") != null){
            resp.sendRedirect("/hello");
        }else{
            PrintWriter writer = resp.getWriter();
            String res = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /></head> " +
                    "<body><form action=\"\\session\" method=\"post\">\n" +
                    "username: <input type=\"text\" name=\"username\"><br>\n" +
                    "Password: <input type=\"password\" name=\"pwd\">\n" +
                     "<input type=\"submit\" value=\"Submit\">" +
                    "</form></body></html>";
            writer.write(res);
            writer.flush();
            //设置返回文档类型
            resp.setContentType("text/html;charset=utf-8");
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        String username = req.getParameter("username");
        String pwd = req.getParameter("pwd");
        System.out.println(username + " " + pwd);
        if(!userDatabase.containsKey(username) || !userDatabase.get(username).equals(pwd)){
            String info = userDatabase.containsKey(username) ? "wrong password":"no user";
            PrintWriter writer = resp.getWriter();
            String res = " <html><body><h1>"+ info + "</h1> <a href=\"/session\">login</a></body></html>";
            writer.write(res);
            writer.flush();
            //设置返回文档类型
            resp.setContentType("text/html;charset=utf-8");
        }else{
            HttpSession session = req.getSession();
            session.setAttribute("username", username);
            resp.sendRedirect("/hello");
        }
    }
}
