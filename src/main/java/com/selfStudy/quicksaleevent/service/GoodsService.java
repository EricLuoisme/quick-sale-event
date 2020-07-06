package com.selfStudy.quicksaleevent.service;

import com.selfStudy.quicksaleevent.dao.GoodsDAO;
import com.selfStudy.quicksaleevent.vo.GoodsVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    GoodsDAO goodsDAO;

    public GoodsService(GoodsDAO goodsDAO) {
        this.goodsDAO = goodsDAO;
    }

    public List<GoodsVO> listGoodsVo() {
        return goodsDAO.listGoodsVO();
    }

    public GoodsVO getGoodsVoByGoodsId(long goodsId) {
        return goodsDAO.getGoodsVoByGoodsId(goodsId);
    }
}
