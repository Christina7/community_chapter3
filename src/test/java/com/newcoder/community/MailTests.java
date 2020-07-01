package com.newcoder.community;

import com.newcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
//启用启动类，即配置类CommunityApplication作为正式环境，以它为配置类
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {
    @Autowired
    private MailClient mailClient;

    //主动去调thymeleaf模板引擎
    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail(){

        //通过协议访问from的邮件服务器，将邮件发送给to
        mailClient.sendMail("joocore82@163.com","TEST","welcome");
    }

    @Test
    public void testHtml(){
        Context context=new Context();
        context.setVariable("username","sunday");

        //就是用模板引擎获取到要发的内容，内容在html中
        String content=templateEngine.process("/mail/demo",context);
        System.out.println(content);

        //用mailClient去发
        mailClient.sendMail("joocore82@163.com","HTML",content);
    }
}
