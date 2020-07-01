package com.newcoder.community.controller;

import com.newcoder.community.service.AlphaService;
import com.newcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
//controller处理浏览器请求，调用service处理业务，业务组件去访问数据库
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;


    @RequestMapping("/hello")
    //加注解声明返回的不是网页，就是普通字符串
    @ResponseBody
    //http://localhost:8080/community/alpha/hello
    // 在application.properties里加了项目路径/community
    public String sayHello() {
        return "hello";
        //如果没有@ResponseBody，返回到hello.html网页
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData() {
        return alphaService.find();
    }

    @RequestMapping("/http")//由handlerMapping负责解析
    //用response对象直接给浏览器返回数据
    //测试获取请求对象HttpServletRequest和响应对象HttpServletResponse
    //声明后，dispatcherServlet调用时，就会自动把那两个对象传给你
    public void http(HttpServletRequest request, HttpServletResponse response) {
        //获取请求数据
        //第一行
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());

        //请求头，若干行数据
        Enumeration<String> enumeration = request.getHeaderNames();//获取所有请求行的key
        //while遍历迭代器对象
        while (enumeration.hasMoreElements()) {
            //请求头是key-value类型，要先拿到key，通过key去取value
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ":" + value);
        }

        //请求体，业务数据，参数
        //如对于/community/http?code=123 控制台打印123
        System.out.println(request.getParameter("code"));

        //返回响应数据
        response.setContentType("text/html;charset=utf-8");
        //通过它封装的输出流，向浏览器输出
        try (
                PrintWriter writer = response.getWriter();//这样可以不在finally中释放writer了
        ) {
            writer.write("<h1>new coder</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //用/students?current=1&limit=10
    //获取用户输入的?后的参数
    //分页，当前第一页，每页最多显示10条数据
    @RequestMapping(method = RequestMethod.GET, path = "/students")
    @ResponseBody
    //用获取请求中带的参数
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        System.out.println("current" + current);
        System.out.println("limit" + limit);
        return "some students";
    }

    //获取路径中带的参数
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody//返回一个字符串
    public String getStudent(@PathVariable("id") int id) {
        System.out.println("id:" + id);
        return "a student";
    }


    //对应的是/community/html/student.html
    @RequestMapping(path="/student",method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name,int age){
        //和表单一致，就能把数据传过来
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    //向浏览器返回响应HTML数据
    @RequestMapping(path="/teacher",method = RequestMethod.GET)
    //不加@ResponseBody，默认返回的就是html，返回ModelAndView
    public ModelAndView getTeacher(){
        //ModelAndView里封装的是DispatcherServlet返回的Model和view两个数据
        ModelAndView mav=new ModelAndView();
        mav.addObject("name","zhangsan");
        mav.addObject("age",30);
        //模板放到templates下
        mav.setViewName("/demo/view");
        return mav;
    }


    //model是对象，DispatcherServlet持有对该对象的引用
    @RequestMapping(value = "/school",method = RequestMethod.GET)
    public String getSchool(Model model){
        //把model的数据装到model参数里，view视图直接返回
        model.addAttribute("name","peking university");
        model.addAttribute("age",100);
        return "/demo/view";
    }

    //响应json数据（异步数据）
    //异步请求，就是当前页面不刷新，但它悄悄访问了服务器/db，返回了结果，这个结果不是html
    //把java对象返回给浏览器，浏览器用js解析这个对象，得到js对象
    //用json实现了java对象——js对象，之间的兼容
    //java对象——>json字符串——>JS对象，可以方便地返回给客户端局部判断的结果
    @RequestMapping(path="/emp",method = RequestMethod.GET)
    @ResponseBody//响应的不是网页，是map，会转为json
    public Map<String,Object> getEmp(){
        //DispatcherServlet会自动把map转为Json字符串，发给浏览器
        Map<String,Object> map=new HashMap<>();
        map.put("name","zhangsan");
        map.put("age",25);
        map.put("salary",8000);
        return map;
    }

    @RequestMapping(value = "/emps",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getEmps(){
        List<Map<String,Object>> list=new ArrayList<>();

        Map<String,Object> map=new HashMap<>();
        map.put("name","zhangsan");
        map.put("age",25);
        map.put("salary",8000);
        list.add(map);

        map=new HashMap<>();
        map.put("name","lisi");
        map.put("age",30);
        map.put("salary",9000);
        list.add(map);

        map=new HashMap<>();
        map.put("name","zhaoliu");
        map.put("age",35);
        map.put("salary",10000);
        list.add(map);

        return list;
    }

    //cookie示例，server对于一个来自client的请求，产生cookie，cookie放在response的头部，返回给c
    @RequestMapping(path="/cookie/set",method =RequestMethod.GET)
    @ResponseBody//返回jason字符串
    public String setCookie(HttpServletResponse response){
        //创建一个cookie
        Cookie cookie=new Cookie("code", CommunityUtil.generateUUID());
        //设置cookie的生效范围
        //即client在访问哪些路径时会发送cookie
        cookie.setPath("/community/alpha");//alpha下都有cookie
        //默认存到浏览器的内存里，一般浏览器关了，cookie就不在了
        //设置cookie的生存时间，存在硬盘里，直到超过时间后无效
        cookie.setMaxAge(60*10);//单位秒，此处是10min

        //发送cookie
        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping(path="/cookie/get",method=RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code")String code){
        System.out.println(code);
        //或者model.addAttribute() 传回给模板
        return "get cookie";
    }

    @RequestMapping(value = "/ajax",method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJsonString(0,"操作成功");

    }


}

