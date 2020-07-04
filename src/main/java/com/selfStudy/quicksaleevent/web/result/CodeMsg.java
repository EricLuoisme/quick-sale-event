package com.selfStudy.quicksaleevent.web.result;

public class CodeMsg {
    private int code;
    private String msg;

    // general errors
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "server error");

    // login errors # 500200
    public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "session not exist or expired");
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211, "user password can not be empty");
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212, "user mobile work as ID, can not be empty");
    public static CodeMsg MOBILE_ERROR = new CodeMsg(500213, "invalid mobile pattern");
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214, "mobile number has not been registered");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "password is wrong");


    private CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
