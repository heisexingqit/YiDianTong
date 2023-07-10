package com.example.yidiantong.bean;

import java.io.Serializable;

public class THomeworkAddEntity implements Serializable {
    private String pointName;
    private String questionId;
    private String baseTypeId;
    private String answer;
    private String bigId;
    private String score;
    private String smallId;
    private String analysis;
    private String resourceOrder;
    private String questionDifficult;
    private String typeName;
    private String pointCode;
    private String tiMian;
    private String questionTextControl;
    private String typeId;
    private String selectFlag;

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getBaseTypeId() {
        return baseTypeId;
    }

    public void setBaseTypeId(String baseTypeId) {
        this.baseTypeId = baseTypeId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getBigId() {
        return bigId;
    }

    public void setBigId(String bigId) {
        this.bigId = bigId;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getSmallId() {
        return smallId;
    }

    public void setSmallId(String smallId) {
        this.smallId = smallId;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getResourceOrder() {
        return resourceOrder;
    }

    public void setResourceOrder(String resourceOrder) {
        this.resourceOrder = resourceOrder;
    }

    public String getQuestionDifficult() {
        return questionDifficult;
    }

    public void setQuestionDifficult(String questionDifficult) {
        this.questionDifficult = questionDifficult;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getPointCode() {
        return pointCode;
    }

    public void setPointCode(String pointCode) {
        this.pointCode = pointCode;
    }

    public String getTiMian() {
        return tiMian;
    }

    public void setTiMian(String tiMian) {
        this.tiMian = tiMian;
    }

    public String getQuestionTextControl() {
        return questionTextControl;
    }

    public void setQuestionTextControl(String questionTextControl) {
        this.questionTextControl = questionTextControl;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getSelectFlag() {
        return selectFlag;
    }

    public void setSelectFlag(String selectFlag) {
        this.selectFlag = selectFlag;
    }
    
    @Override
    public String toString() {
        return "THomeworkAddEntity{" +
                "pointName='" + pointName + '\'' +
                ", questionId='" + questionId + '\'' +
                ", baseTypeId='" + baseTypeId + '\'' +
                ", answer='" + answer + '\'' +
                ", bigId='" + bigId + '\'' +
                ", score='" + score + '\'' +
                ", smallId='" + smallId + '\'' +
                ", analysis='" + analysis + '\'' +
                ", resourceOrder='" + resourceOrder + '\'' +
                ", questionDifficult='" + questionDifficult + '\'' +
                ", typeName='" + typeName + '\'' +
                ", pointCode='" + pointCode + '\'' +
                ", tiMian='" + tiMian + '\'' +
                ", questionTextControl='" + questionTextControl + '\'' +
                ", typeId='" + typeId + '\'' +
                ", selectFlag='" + selectFlag + '\'' +
                '}';
    }

    public String toData() {
        return '{' +
                "\"pointName\":\"" + pointName + '\"' +
                ", \"questionId\":\"" + questionId + '\"' +
                ", \"baseTypeId\":\"" + baseTypeId + '\"' +
                ", \"bigId\":\"" + bigId + '\"' +
                ", \"score\":\"" + score + '\"' +
                ", \"smallId\":\"" + smallId + '\"' +
                ", \"questionDifficult\":\"" + questionDifficult + '\"' +
                ", \"typeName\":\"" + typeName + '\"' +
                ", \"pointCode\":\"" + pointCode + '\"' +
                ", \"questionTextControl\":\"" + questionTextControl + '\"' +
                ", \"typeId\":\"" + typeId + '\"' +
                '}';
    }
}