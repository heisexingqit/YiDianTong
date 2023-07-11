package com.example.yidiantong.bean;

import java.io.Serializable;

public class StuAnswerEntity implements Serializable {
    private int order;
    private String questionId;
    private String status;
    private String stuAnswer;
    private String teaScore;

    public StuAnswerEntity(int order){
        this.order = order;
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
        return "SAE{" +
                "order=" + order +
                "}";
    }
}
