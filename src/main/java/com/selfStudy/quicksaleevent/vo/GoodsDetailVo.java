package com.selfStudy.quicksaleevent.vo;

import com.selfStudy.quicksaleevent.domain.model.QuickSaleUser;

public class GoodsDetailVo {

    private GoodsVo goods;
    private int quickSaleEventStatus = 0;
    private int remainSeconds = 0;
    private QuickSaleUser user;

    public GoodsVo getGoods() {
        return goods;
    }

    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }

    public int getQuickSaleEventStatus() {
        return quickSaleEventStatus;
    }

    public void setQuickSaleEventStatus(int quickSaleEventStatus) {
        this.quickSaleEventStatus = quickSaleEventStatus;
    }

    public int getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }

    public QuickSaleUser getUser() {
        return user;
    }

    public void setUser(QuickSaleUser user) {
        this.user = user;
    }
}
