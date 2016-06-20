package io.base.model;

/**
 * Created by gaochao on 2016/2/17.
 */
public class ResultBean {
    private String data;
    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public String getDesc() {
        return msg;
    }

    public ResultBean setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public ResultBean setDesc(String msg) {
        this.msg = msg;
        return this;
    }

    public boolean isOk(){ return code == OK_CODE; }

    public void setOk(){ code = OK_CODE; }

    public ResultBean setNo(){
        code = 1;
        return this;
    }

    private static final int OK_CODE = 20000;
}
