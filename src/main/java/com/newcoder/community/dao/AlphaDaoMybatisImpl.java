package com.newcoder.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository//纳入容器
@Primary//会被优先装配
public class AlphaDaoMybatisImpl implements AlphaDao {
    @Override
    public String select() {
        return "myBatis";
    }
}
