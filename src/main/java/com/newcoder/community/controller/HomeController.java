package com.newcoder.community.controller;

import com.newcoder.community.entity.DiscussPost;
import com.newcoder.community.entity.Page;
import com.newcoder.community.entity.User;
import com.newcoder.community.service.DiscussPostService;
import com.newcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;

    @RequestMapping(path="/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        //dispatcherServlet会初始化，自动实例化model,page，会把page注入给model
        //所以在thymeleaf中就可以直接访问Page对象中的数据

        //服务器要设置总行数，首页所以不以userId来查询，因此传入0
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");//这样页面上就可以复用该路径


        //要通过model携带数据给前端模板
        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());

        //把每条帖子+用户具体信息都放到map里，最后把这些都放到list里
        List<Map<String,Object>> discussPosts=new ArrayList<>();

        if(list!=null){
            for(DiscussPost post:list){
                Map<String,Object> map=new HashMap<>();
                map.put("post",post);

                //针对每个DiscussPost，拿到userId后查到具体的用户信息
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);
                discussPosts.add(map);
            }
        }

        //最后将要展现的结果装到model里，页面view才能得到
        //model是联系controller和view的纽带
        model.addAttribute("discussPosts",discussPosts);

        return "/index";//返回的是模板路径,templates下的index.html
    }

}
