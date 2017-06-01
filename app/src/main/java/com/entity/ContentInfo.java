package com.entity;

public class ContentInfo extends BaseModel {
    private String id;          //ID
    private String content;     //CONTENT
    private String rowId;        //ROW_ID
    private String rowSort;     //ROW_SORT
    private String remark;      //REMARK

    public ContentInfo() {
    }

    public ContentInfo(String content, String rowId) {
        this.content = content;
        this.rowId = rowId;
    }
    public ContentInfo(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public String getRowSort() {
        return rowSort;
    }

    public void setRowSort(String rowSort) {
        this.rowSort = rowSort;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


}
