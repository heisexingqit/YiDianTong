package com.example.yidiantong.bean;

import java.io.Serializable;

public class HomeworkMarkedEntity implements Serializable {
    private String standardAnswer;
    private String description;
    private String typeName;
    private String statusImage;
    private double fullScore;
    private double score;
    private String tiMian;
    private String analysis;
    private String stuAnswer;
    private String showAnswerFlag;

    public String getShowAnswerFlag() {
        return showAnswerFlag;
    }

    public void setShowAnswerFlag(String showAnswerFlag) {
        this.showAnswerFlag = showAnswerFlag;
    }

    public String getStandardAnswer() {
        return standardAnswer;
    }

    public void setStandardAnswer(String standardAnswer) {
        this.standardAnswer = standardAnswer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getStatusImage() {
        return statusImage;
    }

    public void setStatusImage(String statusImage) {
        this.statusImage = statusImage;
    }

    public double getFullScore() {
        return fullScore;
    }

    public void setFullScore(double fullScore) {
        this.fullScore = fullScore;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getTiMian() {
        return tiMian;
    }

    public void setTiMian(String tiMian) {
        this.tiMian = tiMian;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getStuAnswer() {
        return stuAnswer;
    }

    public void setStuAnswer(String stuAnswer) {
        this.stuAnswer = stuAnswer;
    }

    @Override
    public String toString() {
        return "HomeworkMarkedEntity{" +
                "standardAnswer='" + standardAnswer + '\'' +
                ", description='" + description + '\'' +
                ", typeName='" + typeName + '\'' +
                ", statusImage='" + statusImage + '\'' +
                ", fullScore=" + fullScore +
                ", score=" + score +
                ", tiMian='" + tiMian + '\'' +
                ", analysis='" + analysis + '\'' +
                ", stuAnswer='" + stuAnswer + '\'' +
                ", showAnswerFlag='" + showAnswerFlag + '\'' +
                '}';
    }
}
