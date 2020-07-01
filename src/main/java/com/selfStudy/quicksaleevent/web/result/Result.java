package com.selfStudy.quicksaleevent.web.result;

public class Result<T> {
    private int code;
    private String msg;
    private T data;

    // for successful calling
    public static <T> Result<T> success(T data) {
        return new Result<>(data);
    }

    // for unsuccessful calling
    public static <T> Result<T> error(CodeMsg cm) {
        return new Result<T>(cm);
    }

    private Result(T data) {
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }

    private Result(CodeMsg cm) {
        if (cm != null) {
            this.code = cm.getCode();
            this.msg = cm.getMsg();
        }

    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
}
