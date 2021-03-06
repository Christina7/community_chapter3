package com.newcoder.community.controller.Interceptor;

import com.newcoder.community.annotation.LoginRequired;
import com.newcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod= (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            if(loginRequired!=null && hostHolder.getUser()==null){
                // 要求登录态访问，但此时请求获取到的登录用户为空，
                // preHandle的处理就是拒绝访问，重定向到登录页面
                // getContextPath()获取项目路径
                response.sendRedirect(request.getContextPath()+"/login");
                return false;
            }
        }

        return true;
    }
}
