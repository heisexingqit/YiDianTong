package com.example.yidiantong.bean;

import java.io.Serializable;
import java.util.Date;

public class ZZXXStuAnswerBean implements Serializable {
    private String taskId;
    private String questionId;
    private String stuAnswer;
    private String score;
    private String createDate;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

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

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public ZZXXStuAnswerBean(String taskId, String questionId, String stuAnswer, String score, String createDate) {
        this.taskId = taskId;
        this.questionId = questionId;
        this.stuAnswer = stuAnswer;
        this.score = score;
        this.createDate = createDate;
    }


    public ZZXXStuAnswerBean() {
    }

    public String toString() {
        return "ZZXXStuAnswerBean{taskId='" + this.taskId + '\'' + ", questionId='" + this.questionId + '\'' + ", stuAnswer='" + this.stuAnswer + '\'' + ", score='" + this.score + '\'' + ", createDate='" + this.createDate + '\'' + '}';
    }
}
