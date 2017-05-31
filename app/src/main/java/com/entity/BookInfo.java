package com.entity;

/**
 * Created by Novice on 2017/5/31.
 */

public class BookInfo extends BaseModel{

    private int no;
    private String name;
    private String details;
    private String buyUrl;

    public BookInfo(int no, String name, String details, String buyUrl) {
        this.name = name;
        this.no = no;
        this.details = details;
        this.buyUrl = buyUrl;
    }
    public BookInfo() {

    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return this.details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getBuyUrl() {
        return this.buyUrl;
    }

    public void setBuyUrl(String buyUrl) {
        this.buyUrl = buyUrl;
    }
}
