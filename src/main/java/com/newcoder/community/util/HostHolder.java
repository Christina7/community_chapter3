package com.newcoder.community.util;

import com.newcoder.community.entity.User;
import org.springframework.stereotype.Component;

//服务器处理多个请求，每个请求跟浏览器开一个线程，要对每个用户的user对象进行存储
//用ThreadLocal，把每个线程对应的请求，这个登录用户，保存在线程内部，线程隔离
//持有用户信息，代替了session
@Component
public class HostHolder {
    private ThreadLocal<User> users=new ThreadLocal<>();
    //ThreadLocal的三个方法get/set/remove

    public void setUsers(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }

}
