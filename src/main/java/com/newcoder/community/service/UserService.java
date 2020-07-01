package com.newcoder.community.service;

import com.newcoder.community.dao.DiscussPostMapper;
import com.newcoder.community.dao.LoginTicketMapper;
import com.newcoder.community.dao.UserMapper;
import com.newcoder.community.entity.DiscussPost;
import com.newcoder.community.entity.LoginTicket;
import com.newcoder.community.entity.User;
import com.newcoder.community.util.CommunityConstant;
import com.newcoder.community.util.CommunityUtil;
import com.newcoder.community.util.MailClient;
import com.newcoder.community.util.SensitiveFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    //MailClient @Component bean injection
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;



    //注入固定的值，而不是bean，用value
    @Value("${community.path.domain}")
    private String domain;

    //项目名
    @Value("${server.servlet.context-path}")
    private String contextPath;


    public User findUserById(int userId){
        return userMapper.selectById(userId);
    }

    public Map<String,Object> register(User user){
        Map<String,Object> map=new HashMap<>();
        if(user==null){
            throw new IllegalArgumentException("参数不能为空");
        }

        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
            return map;
        }

        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }

        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }


        User user1 = userMapper.selectByName(user.getUsername());
        if(user1!=null){
            map.put("usernameMsg","该账号已存在");
            return map;
        }

        user1=userMapper.selectByEmail(user.getEmail());
        if(user1!=null){
            map.put("emailMsg","该邮箱已被注册");
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        //对密码进行一层覆盖
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        //头像 images.nowcoder.com/head/%dt.png %d是占位符，范围0-1000
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png"
                ,new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        //要将该用户添加到库里
        userMapper.insertUser(user);


        //需要发送激活邮件给用户，激活邮件的模板用/mail/activation
        //要将该静态的html改造成模板

        Context context=new Context();
        //这里set的变量，是能让template接收到的变量
        context.setVariable("email",user.getEmail());

        //激活url的格式 http://localhost:8080/community/activation/{id}/code
        String url=domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);

        //把填了变量的context传给这个邮件模板
        String content = templateEngine.process("/mail/activation", context);
        //发送邮件
        mailClient.sendMail(user.getEmail(),"激活账号",content);

        return map;
    }

    public int activation(int userId,String code){
        User user=userMapper.selectById(userId);
        //先根据id查到用户，检查用户是否已经激活
        if(user.getStatus()==1){
            return ACTIVATION_REPEAT;
        }

        // 传来的激活码对不对，是不是伪造的
        //正确的激活码，激活，修改用户状态，跳转到登录接口
        else if(code.equals(user.getActivationCode())){
            userMapper.updateStatus(userId,1);
            return ACTIVATON_SUCCESS;
        }
        else{
            //激活码是错的
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String,Object> login(String username,String password,int expiredSeconds){
        Map<String,Object> map=new HashMap<>();

        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","username cannot be null");
            return map;
        }

        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","password cannot be null");
            return map;
        }

        //verify username
        User user=userMapper.selectByName(username);
        if(user==null){
            map.put("usernameMsg","account doesn't exist");
            return map;
        }

        if(user.getStatus()==0){
            map.put("usernameMsg","account hasn't been activated");
            return map;
        }

        //verify password
        String upassword=CommunityUtil.md5(password+user.getSalt());
        if(!user.getPassword().equals(upassword)){
            map.put("passwordMsg","password wrong");
            return map;
        }

        //generate ticket for user
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());//random
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));

        //insert loginTicket for user into database
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket",loginTicket.getTicket());

        return map;
    }

    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket,1);
    }

    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }

    //更新图像路径
    public int updateHeader(int userId,String headerUrl){
        return userMapper.updateHeaderUrl(userId,headerUrl);
    }



}
