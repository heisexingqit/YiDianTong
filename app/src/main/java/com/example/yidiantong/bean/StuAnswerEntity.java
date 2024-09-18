package com.example.yidiantong.bean;

import java.io.Serializable;

public class StuAnswerEntity implements Serializable {
    private int order;
    private String questionId;
    private String id;
    private String status;
    private String stuAnswer;
    private String teaScore;
    private String answerTime;
    private String useTime;
    private String optionTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getOptionTime() {
        return optionTime;
    }

    public void setOptionTime(String optionTime) {
        this.optionTime = optionTime;
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

    public StuAnswerEntity(int order) {
        this.order = order;
    }

    public StuAnswerEntity() {
        this.stuAnswer = "";
        this.useTime = "0";
    }

    public StuAnswerEntity(int order, String questionId, String status, String stuAnswer, String teaScore, String answerTime, String useTime) {
        this.order = order;
        this.questionId = questionId;
        this.status = status;
        this.stuAnswer = stuAnswer;
        this.teaScore = teaScore;
        this.answerTime = answerTime;
        this.useTime = useTime;
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
                ", id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", stuAnswer='" + stuAnswer + '\'' +
                ", teaScore='" + teaScore + '\'' +
                ", answerTime='" + answerTime + '\'' +
                ", useTime='" + useTime + '\'' +
                ", optionTime='" + optionTime + '\'' +
                '}';
    }
}
