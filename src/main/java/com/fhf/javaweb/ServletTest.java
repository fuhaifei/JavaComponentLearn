package com.fhf.javaweb;





import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

@WebServlet(urlPatterns = "/hello")
public class ServletTest extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Enumeration<String> parameterNames = req.getParameterNames();
        while(parameterNames.hasMoreElements()){
            String curParameter = parameterNames.nextElement();
            System.out.println(curParameter + ":" + req.getParameter(curParameter));
        }
        //返回继续写入hello world
        resp.setContentType("text/html;charset=utf-8");
        PrintWriter writer = resp.getWriter();
        HttpSession session = req.getSession();
        if(session.getAttribute("username") != null){
            System.out.println(session.getAttribute("username"));
            writer.write("<html><body><h1>Hello!!"  +session.getAttribute("username")+ "</h1></body></html>");
        }else{
            writer.write("<html><body><h1>Hello! you need to log in</h1> " +
                    "<a href=\"/session\">login</a></body></html>");
        }
        writer.flush();
    }
}
