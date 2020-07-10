package com.selfStudy.quicksaleevent.redis;

public class QuickSaleKey extends BasePrefix {

    public QuickSaleKey(String prefix) {
        super(prefix); // order key will not expired
    }

    public static QuickSaleKey isOutOfStock = new QuickSaleKey("goos"); // good out of stock
}
