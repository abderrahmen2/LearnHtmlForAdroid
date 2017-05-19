package com.utils;

import java.util.List;

public class ResultListData<T> {
    private boolean success;
    private List<T> list;
    private String message;


    public ResultListData(boolean success, List<T> list) {
        this.success = success;
        this.list = list;
    }

    public ResultListData(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResultListData() {
    }
}
