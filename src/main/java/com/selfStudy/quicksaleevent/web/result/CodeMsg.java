package com.selfStudy.quicksaleevent.web.result;

public class CodeMsg {
    private int code;
    private String msg;

    // general errors
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "server error");

    // login errors # 500200


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
