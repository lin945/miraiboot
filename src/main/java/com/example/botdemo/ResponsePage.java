package com.example.botdemo;


/**
 * @author lin945
 * @date 2020/6/13 0:44
 */

public class ResponsePage {
    private String state;
    private String message;
    private Object data;

    public ResponsePage ok(Object data){
    this.state = "200";
    this.message = "ok";
    this.data = data;
    return this;
}
    public ResponsePage failed(Object data){
        this.state = "400";
        this.message = "erro";
        this.data = data;
        return this;
    }

    public ResponsePage() {
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponsePage{" +
                "state='" + state + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
