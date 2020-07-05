package com.selfStudy.quicksaleevent.vo;

import com.selfStudy.quicksaleevent.domain.model.Goods;

import java.util.Date;

public class GoodsVO extends Goods {

    /**
     * by extending Goods + 4 fields from QuickSalesGoods,
     * we can show them simultaneously on main page
     */

    private Double quicksalePrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;

    public Double getQuicksalePrice() {
        return quicksalePrice;
    }

    public void setQuicksalePrice(Double quicksalePrice) {
        this.quicksalePrice = quicksalePrice;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
