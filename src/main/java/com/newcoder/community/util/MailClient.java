package com.newcoder.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailClient {
    private static final Logger logger= LoggerFactory.getLogger(MailClient.class);
    //JavaMailSender
    @Autowired
    private JavaMailSender mailSender;

    //发件人就是username，注入
    //通过key值注入
    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to,String subject,String content){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            //用helper去构建MimeMessage
            MimeMessageHelper helper=new MimeMessageHelper(message);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            //加了true，支持HTML文本，支持发送html文件
            helper.setText(content,true);;
            //最后从helper中获取构建好的MimeMessage，由MailSender发送
            mailSender.send(helper.getMimeMessage());

        } catch (MessagingException e) {
            logger.error("发送信息失败"+e.getMessage());
        }

    }
}
