package com.selfStudy.quicksaleevent.dao;

import com.selfStudy.quicksaleevent.domain.model.QuickSaleGoods;
import com.selfStudy.quicksaleevent.vo.GoodsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GoodsDao {

    @Select("select g.*, mg.stock_count, mg.start_date, mg.end_date,mg.quicksale_price from quicksale_goods mg left join goods g on mg.goods_id = g.id")
    public List<GoodsVO> listGoodsVO();

    @Select("select g.*, mg.stock_count, mg.start_date, mg.end_date,mg.quicksale_price from quicksale_goods mg left join goods g on mg.goods_id = g.id where g.id = #{goodsId}")
    public GoodsVO getGoodsVoByGoodsId(@Param("goodsId") long goodsId);

    @Update("update quicksale_goods set stock_count = stock_count - 1 where goods_id = #{goodsId} and stock_count > 0")
    public int reduceStock(QuickSaleGoods g);
}
