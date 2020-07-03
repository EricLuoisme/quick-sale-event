package com.selfStudy.quicksaleevent.redis;

public class UserKey extends BasePrefix {

    private UserKey(String prefix) {
        super(prefix);
    }

    // only at the first calling, we would create it with the private constructor
    public static UserKey getById = new UserKey("id");
    public static UserKey getByName = new UserKey("name");
}
