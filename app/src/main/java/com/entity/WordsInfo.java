package com.entity;

public class WordsInfo extends BaseModel {
    private String id;                //ID
    private String userName;        //USER_NAME,用戶名
    private String rowId;            //ROW_ID,留言对应知识内容模块的编号
    private String wordsContent;    //WORDS_CONTENT ，留言内容
    private String wordsTime;        //WORDS_TIME ，留言时间
    private String answerUserName;    //ANSWER_USER_NAME，被回复这用户名
    private String state;            //STATE  状态
    private String rowNum;           //楼号


    public String getRowNum() {
        return rowNum;
    }

    public void setRowNum(String rowNum) {
        this.rowNum = rowNum;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public String getWordsContent() {
        return wordsContent;
    }

    public void setWordsContent(String wordsContent) {
        this.wordsContent = wordsContent;
    }

    public String getWordsTime() {
        return wordsTime;
    }

    public void setWordsTime(String wordsTime) {
        this.wordsTime = wordsTime;
    }

    public String getAnswerUserName() {
        return answerUserName;
    }

    public void setAnswerUserName(String answerUserName) {
        this.answerUserName = answerUserName;
    }


}
