package com.newcoder.community.controller.Interceptor;

import com.newcoder.community.dao.LoginTicketMapper;
import com.newcoder.community.entity.LoginTicket;
import com.newcoder.community.entity.User;
import com.newcoder.community.service.UserService;
import com.newcoder.community.util.CookieUtil;
import com.newcoder.community.util.HostHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    private static final Logger logger= LoggerFactory.getLogger(LoginTicketInterceptor.class);

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.debug("preHandle:"+handler.toString());
        System.out.println("preHandle:"+handler.toString());
        //从cookie中获取ticket的值
        String ticket = CookieUtil.getValue(request, "ticket");

        if(ticket!=null){
            //valid?
            //interceptor controller--->service
            LoginTicket loginTicket=userService.findLoginTicket(ticket);
            if(loginTicket!=null && loginTicket.getStatus()==0
                    &&loginTicket.getExpired().after(new Date())){
                //到期时间在当前时间之后
                //根据ticket值查找到用户
                User user = userService.findUserById(loginTicket.getUserId());
                System.out.println("preHandle```````````User:"+user);
                //在本次请求持有用户
                hostHolder.setUsers(user);
            }
        }

        return true;
    }

    //controller后，template之前
    //Model是联系View和Controller的纽带，这里是在ModelAndView上加上user
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.debug("postHandle"+handler.toString());
        System.out.println("postHandle:"+handler.toString());

        //还是这个当前线程，从hostHolder中获取user
        User loginUser = hostHolder.getUser();

        System.out.println("postHandle```````````loginUser:"+loginUser);
        if(modelAndView!=null && loginUser!=null){
            modelAndView.addObject("loginUser",loginUser);
        }

    }

    //template后，在请求结束后，清理掉不需要的用户数据
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("afterCompletion:"+handler.toString());

        hostHolder.clear();
    }
}
