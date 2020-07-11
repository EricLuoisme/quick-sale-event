package com.selfStudy.quicksaleevent.service;

import com.selfStudy.quicksaleevent.dao.GoodsDao;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleGoods;
import com.selfStudy.quicksaleevent.vo.GoodsVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    GoodsDao goodsDao;

    public GoodsService(GoodsDao goodsDao) {
        this.goodsDao = goodsDao;
    }


    public List<GoodsVo> listGoodsVo() {
        return goodsDao.listGoodsVO();
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public boolean reduceStock(GoodsVo goods) {
        QuickSaleGoods g = new QuickSaleGoods();
        g.setGoodsId(goods.getId());
        int stock = goodsDao.reduceStock(g);
        return stock > 0;
    }

    public void resetStock(List<GoodsVo> goodsList) {
        for (GoodsVo goods : goodsList) {
            QuickSaleGoods g = new QuickSaleGoods();
            g.setGoodsId(goods.getId());
            g.setStockCount(goods.getStockCount());
            goodsDao.resetStock(g);
        }
    }
}
