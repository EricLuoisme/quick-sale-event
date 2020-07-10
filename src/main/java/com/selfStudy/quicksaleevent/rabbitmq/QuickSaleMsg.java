package com.selfStudy.quicksaleevent.rabbitmq;

import com.selfStudy.quicksaleevent.domain.model.QuickSaleUser;

public class QuickSaleMsg {
    private QuickSaleUser user;
    private long goodsId;

    public QuickSaleUser getUser() {
        return user;
    }

    public void setUser(QuickSaleUser user) {
        this.user = user;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }
}
