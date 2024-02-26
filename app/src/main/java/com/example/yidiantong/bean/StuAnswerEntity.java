package com.example.yidiantong.bean;

import java.io.Serializable;

public class StuAnswerEntity implements Serializable {
    private int order;
    private String questionId;
    private String status;
    private String stuAnswer;
    private String teaScore;


    public StuAnswerEntity(int order) {
        this.order = order;
    }

    public StuAnswerEntity() {

    }

    public StuAnswerEntity(int order, String questionId, String status, String stuAnswer, String teaScore) {
        this.order = order;
        this.questionId = questionId;
        this.status = status;
        this.stuAnswer = stuAnswer;
        this.teaScore = teaScore;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStuAnswer() {
        return stuAnswer;
    }

    public void setStuAnswer(String stuAnswer) {
        this.stuAnswer = stuAnswer;
    }

    public String getTeaScore() {
        return teaScore;
    }

    public void setTeaScore(String teaScore) {
        this.teaScore = teaScore;
    }

    @Override
    public String toString() {
        return "StuAnswerEntity{" +
                "order=" + order +
                ", questionId='" + questionId + '\'' +
                ", status='" + status + '\'' +
                ", stuAnswer='" + stuAnswer + '\'' +
                ", teaScore='" + teaScore + '\'' +
                '}';
    }
}
