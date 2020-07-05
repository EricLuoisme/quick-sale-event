package com.selfStudy.quicksaleevent.redis;

public class QuickSaleUserKey extends BasePrefix {

    public static final int TOKEN_EXPIRE = 3600 * 24 * 2; // for 2 days

    private QuickSaleUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    // only at the first calling, we would create it with the private constructor
    public static QuickSaleUserKey token = new QuickSaleUserKey(TOKEN_EXPIRE, "tk");
}
