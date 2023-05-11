package com.example.yidiantong.bean;

import java.io.Serializable;

public class THomeworkMarkedEntity implements Serializable {
    private String stuscore;
    private String paperId;
    private String order;
    private String status;
    private String questionID;
    private String shitiShow;
    private String userName;
    private String questionScore;
    private String questionType;
    private String stuAnswer;
    private String orderCount;
    private String shitiAnswer;
    private String typeName;
    private String shitiAnalysis;

    public String getShitiAnalysis() {
        return shitiAnalysis;
    }

    public void setShitiAnalysis(String shitiAnalysis) {
        this.shitiAnalysis = shitiAnalysis;
    }

    public String getStuscore() {
        return stuscore;
    }

    public void setStuscore(String stuscore) {
        this.stuscore = stuscore;
    }

    public String getPaperId() {
        return paperId;
    }

    public void setPaperId(String paperId) {
        this.paperId = paperId;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public String getShitiShow() {
        return shitiShow;
    }

    public void setShitiShow(String shitiShow) {
        this.shitiShow = shitiShow;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getQuestionScore() {
        return questionScore;
    }

    public void setQuestionScore(String questionScore) {
        this.questionScore = questionScore;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getStuAnswer() {
        return stuAnswer;
    }

    public void setStuAnswer(String stuAnswer) {
        this.stuAnswer = stuAnswer;
    }

    public String getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(String orderCount) {
        this.orderCount = orderCount;
    }

    public String getShitiAnswer() {
        return shitiAnswer;
    }

    public void setShitiAnswer(String shitiAnswer) {
        this.shitiAnswer = shitiAnswer;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return "THomeworkMarkedEntity{" +
                "stuscore='" + stuscore + '\'' +
                ", paperId='" + paperId + '\'' +
                ", order='" + order + '\'' +
                ", status='" + status + '\'' +
                ", questionID='" + questionID + '\'' +
                ", shitiShow='" + shitiShow + '\'' +
                ", userName='" + userName + '\'' +
                ", questionScore='" + questionScore + '\'' +
                ", questionType='" + questionType + '\'' +
                ", stuAnswer='" + stuAnswer + '\'' +
                ", orderCount='" + orderCount + '\'' +
                ", shitiAnswer='" + shitiAnswer + '\'' +
                ", typeName='" + typeName + '\'' +
                ", shitiAnalysis='" + shitiAnalysis + '\'' +
                '}';
    }
}
