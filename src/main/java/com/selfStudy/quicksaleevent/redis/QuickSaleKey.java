package com.selfStudy.quicksaleevent.redis;

public class QuickSaleKey extends BasePrefix {

    public QuickSaleKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static QuickSaleKey isOutOfStock = new QuickSaleKey(0, "goos"); // good out of stock, should not expired
    public static QuickSaleKey getQuicksalePath = new QuickSaleKey(60, "path");
}
