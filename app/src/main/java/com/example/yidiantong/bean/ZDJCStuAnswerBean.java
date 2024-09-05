package com.example.yidiantong.bean;

import java.io.Serializable;

public class ZDJCStuAnswerBean implements Serializable {
    private String questionId;
    private String stuAnswer;
    private String queAnswer;
    private String score;
    private String queScore;
    private String createDate;
    private String baseTypeId;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getStuAnswer() {
        return stuAnswer;
    }

    public void setStuAnswer(String stuAnswer) {
        this.stuAnswer = stuAnswer;
    }

    public String getQueAnswer() {
        return queAnswer;
    }

    public void setQueAnswer(String queAnswer) {
        this.queAnswer = queAnswer;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getQueScore() {
        return queScore;
    }

    public void setQueScore(String queScore) {
        this.queScore = queScore;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getBaseTypeId() {
        return baseTypeId;
    }

    public void setBaseTypeId(String baseTypeId) {
        this.baseTypeId = baseTypeId;
    }

    public ZDJCStuAnswerBean(String questionId, String stuAnswer, String queAnswer, String score, String queScore, String createDate, String baseTypeId) {
        this.questionId = questionId;
        this.stuAnswer = stuAnswer;
        this.queAnswer = queAnswer;
        this.score = score;
        this.queScore = queScore;
        this.createDate = createDate;
        this.baseTypeId = baseTypeId;
    }

    public ZDJCStuAnswerBean() {
    }

    public String toString() {
        return "ZDJCStuAnswerBean{questionId='" + this.questionId + '\'' + ", stuAnswer='" + this.stuAnswer + '\'' + ", queAnswer='" + this.queAnswer + '\'' + ", score='" + this.score + '\'' + ", queScore='" + this.queScore + '\'' + ", createDate='" + this.createDate + '\'' + ", baseTypeId='" + this.baseTypeId + '\'' + '}';
    }
}
