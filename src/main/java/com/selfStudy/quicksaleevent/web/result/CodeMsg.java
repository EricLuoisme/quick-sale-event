package com.selfStudy.quicksaleevent.web.result;

public class CodeMsg {
    private int code;
    private String msg;

    // General Errors
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "server error");
    public static CodeMsg BIND_ERROR = new CodeMsg(500101, "Bind Error : %s");
    public static CodeMsg REQUEST_ILLEGAL = new CodeMsg(500102, "Invalid request");
    public static CodeMsg ACCESS_LIMIT_REACHED = new CodeMsg(500103, "Over-visiting");

    // Login Module Errors # 500200
    public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "session not exist or expired");
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211, "user password can not be empty");
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212, "user mobile work as ID, can not be empty");
    public static CodeMsg MOBILE_ERROR = new CodeMsg(500213, "invalid mobile pattern");
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214, "mobile number has not been registered");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "password is wrong");

    // Goods Module Errors # 500300

    // Order Module Errors # 500400
    public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(500400, "Order is not exist");

    // Quick Sale Module Errors # 500500
    public static CodeMsg EVENT_STORAGE_EMPTY = new CodeMsg(500500, "No more storage of current goods in this event");
    public static CodeMsg CANNOT_BUY_TWICE = new CodeMsg(500501, "A user cannot buy same item twice in same event");
    public static CodeMsg QUICK_SALE_FAIL = new CodeMsg(500502, "quick sale fail");


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

    public CodeMsg fillArgs(Object... args) {
        int code = this.code;
        String msg = String.format(this.msg, args); // add args into the message
        return new CodeMsg(code, msg);
    }
}
