package com.newcoder.community.dao;

import com.newcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DiscussPostMapper {
    //首页的所有帖子，首页不考虑userId=0，考虑将来调取某人的全部帖子再拼到sql里
    //动态sql
    //offset起始行行号 limit每页最多显示多少条数据
   List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);

   //总共多少数据
    int selectDiscussPostRows(@Param("userId")int userId);

    //发布帖子
    int insertDiscussPost(DiscussPost discussPost);

    //查询帖子详情
    DiscussPost selectDiscussPostById(int id);



}
