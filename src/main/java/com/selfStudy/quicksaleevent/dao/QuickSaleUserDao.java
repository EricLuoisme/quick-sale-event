package com.selfStudy.quicksaleevent.dao;

import com.selfStudy.quicksaleevent.domain.model.QuickSaleUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface QuickSaleUserDao {
    /**
     * for communicate with database, by using mybatis, to table quicksale_user
     */

    @Select("select * from quicksale_user where id = #{id}")
    public QuickSaleUser getById(@Param("id") long id);

    @Update("update quicksale_user set password = #{password} where id = #{id}")
    public void update(QuickSaleUser toBeUpdate);
}
