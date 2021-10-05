package com.e.routetest;

public class Board {
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

    public String getDestiny() {
        return destiny;
    }

    public void setDestiny(String destiny) {
        this.destiny = destiny;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public int getThemeID() {
        return themeID;
    }

    public void setThemeID(int themeID) {
        this.themeID = themeID;
    }

    public int getRouteID() {
        return routeID;
    }

    public void setRouteID(int routeID) {
        this.routeID = routeID;
    }

    public String getBoardDate() {
        return boardDate;
    }

    public void setBoardDate(String boardDate) {
        this.boardDate = boardDate;
    }

    public int getCurrentP() {
        return currentP;
    }

    public void setCurrentP(int currentP) {
        this.currentP = currentP;
    }

    public int getMaxP() {
        return maxP;
    }

    public void setMaxP(int maxP) {
        this.maxP = maxP;
    }

    public String getAppliT() {
        return appliT;
    }

    public void setAppliT(String appliT) {
        this.appliT = appliT;
    }

    public Board(int boardID, String boardTitle, String userID, String nickName, String destiny, String arrival, int themeID, int routeID, String boardDate, int currentP, int maxP, String appliT) {
        this.boardID = boardID;
        this.boardTitle = boardTitle;
        this.userID = userID;
        this.nickName = nickName;
        this.destiny = destiny;
        this.arrival = arrival;
        this.themeID = themeID;
        this.routeID = routeID;
        this.boardDate = boardDate;
        this.currentP = currentP;
        this.maxP = maxP;
        this.appliT = appliT;
    }

    int boardID;
    String boardTitle;
    String userID;
    String nickName;
    String destiny;
    String arrival;
    int themeID;
    int routeID;
    String boardDate;
    int currentP;
    int maxP;
    String appliT;
}
