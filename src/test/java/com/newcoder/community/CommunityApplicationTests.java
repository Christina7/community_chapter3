package com.newcoder.community;

import com.newcoder.community.dao.AlphaDao;
import com.newcoder.community.dao.AlphaDaoHibernateImpl;
import com.newcoder.community.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
//启用启动类，即配置类CommunityApplication作为正式环境，以它为配置类
@ContextConfiguration(classes = CommunityApplication.class)
//ioc容器是被自动创建的，哪个类想要得到容器，就实现ApplicationContextAware接口
public class CommunityApplicationTests implements ApplicationContextAware {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //spring容器的顶层接口BeanFactory，ApplicationContext是它的子接口，方法功能更强
        this.applicationContext=applicationContext;
    }

    //测试方法使用spring容器
    @Test
    public void testApplicationContext(){
        System.out.println(applicationContext);
        //写接口的好处在于，实现类变了，这里的接口不用变
        AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
        //按类型去获取bean，找这个接口对应的实现类，但此时有两个，有歧义
        System.out.println(alphaDao.select());

        //指定bean的名字
        alphaDao=applicationContext.getBean("alphaHibernate", AlphaDaoHibernateImpl.class);
        System.out.println(alphaDao.select());
    }

    @Test
    public void testBeanManagement(){
        //被spring容器管理的bean默认是单例的
        AlphaService alphaService=applicationContext.getBean(AlphaService.class);
        System.out.println(alphaService);
    }

    //装配jar包里的第三方bean，通过自己写配置类，通过bean注解进行声明
    @Test
    public void testBeanConfig(){
        SimpleDateFormat simpleDateFormat= (SimpleDateFormat) applicationContext.getBean("simpleDateFormat");
        System.out.println(simpleDateFormat.format(new Date()));
    }

    //依赖注入，直接拿到这个bean来用
    @Autowired
    @Qualifier("alphaHibernate")//按bean的名字去匹配
    //希望程序把AD注入给这个属性aD
    private AlphaDao alphaDao;
    //若当前bean依赖的是接口，则底层实现不直接耦合

    @Autowired
    private SimpleDateFormat simpleDateFormat;

    @Autowired
    private AlphaService alphaService;

    @Test
    public void testDI(){
        //测试依赖注入，直接用成员变量取得这个bean
        System.out.println(alphaService);
        System.out.println(alphaDao);
        System.out.println(simpleDateFormat);//java.text.SimpleDateFormat@4f76f1a0
        //直接用这个bean
        System.out.println(simpleDateFormat.format(new Date()));
    }

}
