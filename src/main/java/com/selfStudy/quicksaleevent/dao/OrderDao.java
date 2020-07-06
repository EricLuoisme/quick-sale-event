package com.selfStudy.quicksaleevent.dao;

import com.selfStudy.quicksaleevent.domain.model.OrderInfo;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleOrder;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderDao {

    @Select("select * from quicksale_order where user_id=#{userId} and goods_id=#{goodsId}")
    public QuickSaleOrder getSpikeOrderByUserIdGoodsId(@Param("userId") long userId, @Param("goodsId") long goodsId);

    @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date) values("
            + "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel}, #{status}, #{createDate} )")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()") // get new insert row's key
    public long insert(OrderInfo orderInfo);

    @Insert("insert into quicksale_order (user_id, goods_id, order_id) values (#{userId}, #{goodsId}, #{orderId})")
    public void insertQuickSaleOrder(QuickSaleOrder miaoshaOrder);
}
