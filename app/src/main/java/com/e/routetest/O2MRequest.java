package com.e.routetest;

public class O2MRequest {
    int boardID;
    String boardTitle;
    String spot1;
    String spot2;
    String userID;
    String nickName;
    String gender;
    String date;
    int age;

    public O2MRequest(int boardID, String boardTitle, String spot1, String spot2, String userID, String nickName, String gender, String date, int age) {
        this.boardID = boardID;
        this.boardTitle = boardTitle;
        this.spot1 = spot1;
        this.spot2 = spot2;
        this.userID = userID;
        this.nickName = nickName;
        this.gender = gender;
        this.date = date;
        this.age = age;
    }

    public int getBoardID() {
        return boardID;
    }

    public void setBoardID(int boardID) {
        this.boardID = boardID;
    }

    public String getBoardTitle() {
        return boardTitle;
    }

    public void setBoardTitle(String boardTitle) {
        this.boardTitle = boardTitle;
    }

    public String getSpot1() {
        return spot1;
    }

    public void setSpot1(String spot1) {
        this.spot1 = spot1;
    }

    public String getSpot2() {
        return spot2;
    }

    public void setSpot2(String spot2) {
        this.spot2 = spot2;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
