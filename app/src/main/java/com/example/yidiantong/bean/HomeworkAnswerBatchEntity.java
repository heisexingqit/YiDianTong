package com.example.yidiantong.bean;

import java.io.Serializable;

public class HomeworkAnswerBatchEntity implements Serializable {
    private String questionId;
    private Integer order;
    private String stuAnswer;
    private String answerTime;
    private String useTime;

    public HomeworkAnswerBatchEntity() {
        answerTime = "";
        useTime = "0";
    }

    public HomeworkAnswerBatchEntity(String questionId, Integer order, String stuAnswer) {
        this.questionId = questionId;
        this.order = order;
        this.stuAnswer = stuAnswer;
        this.answerTime = "";
        this.useTime = "0";
    }

    public String getStuAnswer() {
        return stuAnswer;
    }

    public void setStuAnswer(String stuAnswer) {
        this.stuAnswer = stuAnswer;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(String answerTime) {
        this.answerTime = answerTime;
    }

    public String getUseTime() {
        return useTime;
    }

    public void setUseTime(String useTime) {
        this.useTime = useTime;
    }

    @Override
    public String toString() {
        return "HomeworkAnswerBatchEntity{" +
                "questionId='" + questionId + '\'' +
                ", order=" + order +
                ", stuAnswer='" + stuAnswer + '\'' +
                ", answerTime='" + answerTime + '\'' +
                ", useTime='" + useTime + '\'' +
                '}';
    }
}
