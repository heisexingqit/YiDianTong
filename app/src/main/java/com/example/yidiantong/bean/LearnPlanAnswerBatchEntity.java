package com.example.yidiantong.bean;

import java.io.Serializable;

public class LearnPlanAnswerBatchEntity implements Serializable {
    private String id;
    private String type;
    private int order;
    private String stuAnswer;
    private String optionTime;
    private String useTime;

    public LearnPlanAnswerBatchEntity() {
        this.type = "que";
        this.optionTime = "";
        this.useTime = "0";
    }

    public LearnPlanAnswerBatchEntity(String id, String type, int order, String stuAnswer) {
        this.id = id;
        this.type = type;
        this.order = order;
        this.stuAnswer = stuAnswer;
        this.optionTime = "";
        this.useTime = "0";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getStuAnswer() {
        return stuAnswer;
    }

    public void setStuAnswer(String stuAnswer) {
        this.stuAnswer = stuAnswer;
    }

    public String getOptionTime() {
        return optionTime;
    }

    public void setOptionTime(String optionTime) {
        this.optionTime = optionTime;
    }

    public String getUseTime() {
        return useTime;
    }

    public void setUseTime(String useTime) {
        this.useTime = useTime;
    }
}
