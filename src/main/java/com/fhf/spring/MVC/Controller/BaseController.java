package com.fhf.spring.MVC.Controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;

/**
 * Web开发相关知识点
 * 1. webapp文件夹和web-inf文件夹的区别
 *  * webapp为web项目的根目录，包含项目的所有文件
 *  * web-inf为webapp的安全目录，只有服务端服务端可以访问，用来放置包含敏感信息的文件（业务逻辑/数据库连接）
 * 2. war 和 war explored
 *  * war发布模式，先打成war包在发布部署
 *  * 直接将webapp文件移动到Tomcat文件夹中，支持热部署
 * 3. 静态资源和动态资源请求区别
 *  * 为了区分静态和动态资源访问，通过在请求后添加.do的方式标识动态资源访问
 * 4. url匹配的优先级问题
 *  * 精准匹配->“/*”->模糊匹配->"/"
 *  * tomcat中两个默认的Servlet：JspServlet基于"*.jsp"匹配，Default基于“/”匹配
 * 5. 拦截器(Interceptor)和过滤器（Filter）的区别
 *  * 拦截器来自于Spring框架，基于反射机制实现；过滤器来自于web框架本身，依赖于web日期
 *  * 执行顺序为：请求进入容器 > 进入过滤器 > 进入 Servlet > 进入拦截器 > 执行控制器（Controller）
 *  * 过滤器通常是用来实现通用功能过滤的，比如：敏感词过滤、字符集编码设置、响应数据压缩；拦截器主要用来实现项目中的业务判断的，比如：登录判断、权限判断、日志记录等
 * Spring MVC 知识补充
 * 1. 获取请求参数的几种方式
 *  * @RequestParam/@RequestHeader/@CookieValue等注解指定获取参数的名称，以及是否强制
 *  * 请求处理方法中参数名与请求参数对应或者对应pojo类属性名，SpingMVC自动解析赋值
 *  * 通过servlet api获取参数值，即方法参数传入HttpServletRequest request
 *  * @RequestBody 在参数上使用获取请求体的内容，若要解析json格式数据，需要引入额外的依赖;
 *                 在方法上使用，表示返回值作为响应体写入到响应中
 * 2. 文件上传：单个文件上传使用MultipartFile，多个文件上传MultipartFile [] multipartFiles（参数值与表单name一致）
 *    断点续传：通过Range、Content-Range、Accept-Ranges、Content-Length四个请求头属性实现
 * 3. SpringMVC执行流程
 *  * DispatchServlet 接收用户发送的Request
 *  * 根据请求URL到HandlerMapper获取对应的处理器(Handler)
 *  * HandlerMapper返回处理器执行链（多个过滤器(HandlerInterceptor)+一个处理器(Handler)）
 *  * DispatchServlet获取到对应的处理执行链后，调用对应HandlerAdapter，由HandlerAdapter调用Handler执行请求处理逻辑
 *  * Handler->HandlerAdapter->DispatchServlet返回请求处理的ModelAndView
 *  * DispatchServlet将View交给ViewResolver进行解析后，将Model绑定到试图上实现视图的渲染
 *  * DispatchServlet将渲染好的视图返回给用户
 * 4. SSM整合的问题 {@link org.springframework.web.servlet.FrameworkServlet}
 *  * Spring的加载由ContextLoaderListener实现，其监听到ServletContext的创建后自动创建WebApplicationContext
 *  * SpringMVC的加载由DispatchServlet实现，其加载后读取Spring的WebApplicationContext容器，创建SpringMVC子容器
 *       {@link org.springframework.web.servlet.FrameworkServlet}的 wac.setParent(parent);
 *  * SpringMVC子容器负责Controller的管理，Spring父容器负责DAO和Service的管理，其中SpringMVC子容器在找不到对应Bean时，能够从
 *    父容器中获取对应Bean,这是为什么Controller中的Service能够实现自动装配
 *  * Mybatis所需的SqlSession和DataSource对象，由Spring容器管理。
 *
 * */
@Controller
public class BaseController {

    @PostConstruct
    public void init() {
        // some init function
    }

    @RequestMapping("/test")
    public String index() {
        //设置视图名称
        return "index";
    }

    @GetMapping("/testParams")
    public String testParam(@RequestParam("id") Integer id,@RequestParam("name") String name){
        System.out.println(id + " " + name);
        return "index";
    }

    @RequestMapping("/testShare")
    public ModelAndView testParam(ModelAndView modelAndView){
         modelAndView.addObject("省", "河南省");
         modelAndView.addObject("市", "洛阳市");
         modelAndView.addObject("区", "涧西区");
         modelAndView.addObject("街道","广文路");
         modelAndView.setViewName("redirect:/testRequestShare");
         return modelAndView;
    }

    @RequestMapping("/testRequestShare")
    public String testParamShare(HttpServletRequest request){
        Enumeration<String> parameterNames = request.getParameterNames();
        while(parameterNames.hasMoreElements()){
            String name = parameterNames.nextElement();
            System.out.println(name + ":" + request.getParameter(name));
        }
        return "index";
    }

    @PostMapping("/testRequestBody")
    public String testRequestBody(@RequestBody String requestBody){
        System.out.println(requestBody);
        return "index";
    }
    @PostMapping("/testRequestBodyLoadJson")
    public String testRequestBodyLoadJson(@RequestBody Map<String, String> map){
        for(Map.Entry<String, String> e: map.entrySet()){
            System.out.println(e.getKey() + ":" +  e.getValue());
        }
        return "index";
    }

    @RequestMapping("/testDownload")
    public ResponseEntity<byte[]> downloadFile(HttpSession session) {
        ServletContext servletContext = session.getServletContext();
        String filePath = servletContext.getRealPath("/static/image/R-C.jpg");

        //获取二进制输入流，并写入到byte数组中
        try {
            InputStream inputStream = new FileInputStream(filePath);
            //输入流
            byte[] inputBytes = new byte[inputStream.available()];
            int read = inputStream.read(inputBytes);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Disposition", "attachment;filename=R-C.jpg");
            return new ResponseEntity<>(inputBytes, httpHeaders, HttpStatus.OK);
        }catch (FileNotFoundException e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/fileUpload")
    public String uploadFile(MultipartFile uploadFile, HttpSession session) throws IOException {
        ServletContext servletContext = session.getServletContext();
        String realPath = servletContext.getRealPath("/static/image");
        System.out.println(realPath);

        //生成新的随机文件名（去重）
        System.out.println(uploadFile.getOriginalFilename());
        String[] fileInfo = uploadFile.getOriginalFilename().split("\\.");
        //连接一个随机的UUID
        fileInfo[0] += UUID.randomUUID().toString();

        //存储到目标文件夹

        uploadFile.transferTo(new File(realPath + File.separator+ fileInfo[0] + "." + fileInfo[1]));
        return "index";
    }
}