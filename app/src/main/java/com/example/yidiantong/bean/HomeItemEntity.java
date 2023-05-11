package com.example.yidiantong.bean;

import android.os.Parcelable;

import java.io.Serializable;

public class HomeItemEntity {

    //类型
    private String topTitle;

    //截止时间
    private String timeStop;

    //得分
    private String teaScore;

    //信息类型
    private String type;

    //内容
    private String content;

    //创建者
    private String createrName;

    private String dbid;

    //分类
    private String typeClass;

    //小标题
    private String bottomTitle;

    private String learnId;

    //来源
    private String from;

    //开始时间
    private String startTime;

    //资源发布时间
    private String time;

    //标题
    private String courseName;

    //图标地址
    private String iconUrl;

    //作业班级平均
    private String averageScore;

    //班级排名
    private String rank;

    //状态图标
    private String statusUrl;

    //学习内容状态
    private int status;

    public String getTopTitle() {
        return topTitle;
    }

    public void setTopTitle(String topTitle) {
        this.topTitle = topTitle;
    }

    public String getTimeStop() {
        return timeStop;
    }

    public void setTimeStop(String timeStop) {
        this.timeStop = timeStop;
    }

    public String getTeaScore() {
        return teaScore;
    }

    public void setTeaScore(String teaScore) {
        this.teaScore = teaScore;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }

    public String getDbid() {
        return dbid;
    }

    public void setDbid(String dbid) {
        this.dbid = dbid;
    }

    public String getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(String typeClass) {
        this.typeClass = typeClass;
    }

    public String getBottomTitle() {
        return bottomTitle;
    }

    public void setBottomTitle(String bottomTitle) {
        this.bottomTitle = bottomTitle;
    }

    public String getLearnId() {
        return learnId;
    }

    public void setLearnId(String learnId) {
        this.learnId = learnId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(String averageScore) {
        this.averageScore = averageScore;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getStatusUrl() {
        return statusUrl;
    }

    public void setStatusUrl(String statusUrl) {
        this.statusUrl = statusUrl;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "HomeItemEntity{" +
                "topTitle='" + topTitle + '\'' +
                ", timeStop='" + timeStop + '\'' +
                ", teaScore='" + teaScore + '\'' +
                ", type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", createrName='" + createrName + '\'' +
                ", dbid='" + dbid + '\'' +
                ", typeClass='" + typeClass + '\'' +
                ", bottomTitle='" + bottomTitle + '\'' +
                ", learnId='" + learnId + '\'' +
                ", from='" + from + '\'' +
                ", startTime='" + startTime + '\'' +
                ", time='" + time + '\'' +
                ", courseName='" + courseName + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", averageScore='" + averageScore + '\'' +
                ", rank='" + rank + '\'' +
                ", statusUrl='" + statusUrl + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
