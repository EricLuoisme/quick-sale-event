package com.selfStudy.quicksaleevent.dao;

import com.selfStudy.quicksaleevent.domain.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDao {
    /**
     * for communicate with database, by using mybatis, to table user
     */

    @Select("select * from user where id= #{id}")
    public User getById(@Param("id") int id);

    @Insert("insert into user(id, name) values(#{id}, #{name})")
    public void insert(User user);
}
