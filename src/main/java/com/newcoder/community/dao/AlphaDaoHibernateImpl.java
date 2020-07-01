package com.newcoder.community.dao;

import org.springframework.stereotype.Repository;

//db,给bean取别名。默认名是首字母小写
@Repository("alphaHibernate")
public class AlphaDaoHibernateImpl implements AlphaDao {
    @Override
    public String select() {
        return "Hibernate";
    }
}
