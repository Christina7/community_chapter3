package com.newcoder.community.service;

import com.newcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
//可以用scope修改该bean的作用范围
//@Scope("prototype")//这样每次访问bean会生成新的实例
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    //bean只会被实例化一次，初始化和销毁一次
    public AlphaService(){
        System.out.println("实例化AlphaService");
    }

    //容器管理这个bean的初始化和销毁
    //让该初始化方法在构造器之后调用，初始化某些数据
    @PostConstruct
    public void init(){
        System.out.println("init");
    }

    @PreDestroy
    public void destroy(){
        System.out.println("destroy");
    }

    public String find(){
        return alphaDao.select();
    }
}
