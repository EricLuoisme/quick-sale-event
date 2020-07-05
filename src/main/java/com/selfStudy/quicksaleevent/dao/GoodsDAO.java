package com.selfStudy.quicksaleevent.dao;

import com.selfStudy.quicksaleevent.vo.GoodsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GoodsDAO {

    @Select("select g.*, mg.stock_count, mg.start_date, mg.end_date,mg.quicksale_price from quicksale_goods mg left join goods g on mg.goods_id = g.id")
    public List<GoodsVO> listGoodsVO();


}
