package com.selfStudy.quicksaleevent.redis;

public interface KeyPrefix {
    public int expireSeconds();

    public String getPrefix();
}
