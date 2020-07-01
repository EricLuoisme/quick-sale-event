package com.selfStudy.quicksaleevent.web.result;

public class Result<T> {
    private int code;
    private String msg;
    private T data;

    // for successful calling
    public static <T> Result<T> success(T data){
        return new Result<>(data);
    }




    public int getCode() {
        return code;
    }

    public Result(T data){
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
