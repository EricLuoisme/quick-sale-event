package com.selfStudy.quicksaleevent.redis;

public class GoodsKey extends BasePrefix {

    private static final int PAGE_CACHE_EXPIRED = 60; // 1 min

    private GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    // only at the first calling, we would create it with the private constructor
    public static GoodsKey getGoodsList = new GoodsKey(PAGE_CACHE_EXPIRED, "gl");
    public static GoodsKey getGoodsDetails = new GoodsKey(PAGE_CACHE_EXPIRED, "gd");
    public static GoodsKey getQuickSalesGoodsStock = new GoodsKey(0, "gs");
}
