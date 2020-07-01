package com.newcoder.community.controller;

import com.newcoder.community.annotation.LoginRequired;
import com.newcoder.community.entity.User;
import com.newcoder.community.service.UserService;
import com.newcoder.community.util.CommunityUtil;
import com.newcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger= LoggerFactory.getLogger(UserController.class);

    //注入一个变量
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;


    //返回个人设置页面，直接回路径
    @LoginRequired
    @RequestMapping(path="/setting",method= RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    //上传头像
    @LoginRequired
    @RequestMapping(path="/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage==null){
            model.addAttribute("error","image cannot be null");
            return "/site/setting";
        }

        String fileName=headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","suffix cannot be null");
            return "/site/setting";
        }

        //random filename
        fileName=CommunityUtil.generateUUID()+suffix;
        //file save dest
        File dest=new File(uploadPath+"/"+fileName);

        //save image
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("upload image failed!"+e.getMessage());
            throw new RuntimeException("server exception",e);
        }

        //update headerUrl
        //把获取的该用户的headerUrl改为这个新的url
        //http://localhost:8080/community/user/header/xxx.png
        User user=hostHolder.getUser();
        String headerUrl=domain+contextPath+"/user/header/"+fileName;
        userService.updateHeader(user.getId(),headerUrl);
        return "redirect:/index";
    }

    //获取头像，从访问路径中获取头像文件名
    // 从容器取得response对象
    //用response将图像返回给浏览器
    @RequestMapping(path="/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //服务器的存放路径
        fileName=uploadPath+"/"+fileName;
        String suffix=fileName.substring(fileName.lastIndexOf("."));

        //响应图片
        response.setContentType("image/"+suffix);

        //文件输入流和response输出流，1024字节的缓冲区
        //从本地头像文件读取，输出到response里
        try(
                FileInputStream fis=new FileInputStream(fileName);
                OutputStream os=response.getOutputStream();
                ) {
            byte[] buffer=new byte[1024];
            int b=0;
            while((b=fis.read(buffer))!=-1){
                os.write(buffer,0,b);
            }
        } catch (Exception e) {
            logger.error("读取头像失败"+e.getMessage());
        }
    }


}
