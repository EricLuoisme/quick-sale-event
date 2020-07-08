package com.selfStudy.quicksaleevent.redis;

public class OrderKey extends BasePrefix{

    public OrderKey(String prefix) {
        super(prefix); // order key will not expired
    }

    public static OrderKey getQuickSaleOrderByUidGid = new OrderKey("qoug");
}
