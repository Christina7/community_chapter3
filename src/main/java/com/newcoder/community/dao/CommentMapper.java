package com.newcoder.community.dao;

import com.newcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CommentMapper {
    //查询所有评论
    List<Comment> selectCommentsByEntity(int entityType,int entityId,int offest,int limit);

    //查询评论数量
    int selectCountByEntity(int entityType,int entityId);
}
