package com.selfStudy.quicksaleevent.access;


import com.selfStudy.quicksaleevent.domain.model.QuickSaleUser;

public class UserContext {

    private static ThreadLocal<QuickSaleUser> userHolder = new ThreadLocal<>();
    // thread local provide thread-wise variable

    public static void setUser(QuickSaleUser user) {
        userHolder.set(user);
    }

    public static QuickSaleUser getUser() {
        return userHolder.get();
    }

}
