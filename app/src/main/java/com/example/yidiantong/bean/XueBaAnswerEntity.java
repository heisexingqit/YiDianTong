package com.example.yidiantong.bean;

import java.io.Serializable;

public class XueBaAnswerEntity {
    private String stuName;
    private String stuAnswer;

    public XueBaAnswerEntity() {
    }

    public XueBaAnswerEntity(String stuName, String stuAnswer) {
        this.stuName = stuName;
        this.stuAnswer = stuAnswer;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public String getStuAnswer() {
        return stuAnswer;
    }

    public void setStuAnswer(String stuAnswer) {
        this.stuAnswer = stuAnswer;
    }

    @Override
    public String toString() {
        return "XueBaAnswerEntity{" +
                "stuName='" + stuName + '\'' +
                ", stuAnswer='" + stuAnswer + '\'' +
                '}';
    }
}
