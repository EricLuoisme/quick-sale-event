package com.selfStudy.quicksaleevent.service;

import com.selfStudy.quicksaleevent.dao.GoodsDao;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleGoods;
import com.selfStudy.quicksaleevent.vo.GoodsVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    GoodsDao goodsDAO;

    public GoodsService(GoodsDao goodsDAO) {
        this.goodsDAO = goodsDAO;
    }

    public List<GoodsVo> listGoodsVo() {
        return goodsDAO.listGoodsVO();
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDAO.getGoodsVoByGoodsId(goodsId);
    }

    public boolean reduceStock(GoodsVo goods) {
        QuickSaleGoods g = new QuickSaleGoods();
        g.setGoodsId(goods.getId());
        int stock = goodsDAO.reduceStock(g);
        return stock > 0;
    }
}
