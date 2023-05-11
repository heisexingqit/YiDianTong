package com.example.yidiantong.bean;

import java.io.Serializable;

public class BookRecyclerEntity implements Serializable {

    // 知识点Id
    public String sourceId;

    // 试题题面
    public String shitiShow;

    // 左上角小题号
    public String num;

    // 答案
    public String shitiAnswer;

    // 所有习题数量
    public String allPage;

    // 试题分数
    public String score;

    // 解析
    public String shitiAnalysis;

    // 平均分
    public String avgScore;

    // 资源类型
    public String sourceType;

    // 试题基类型
    public String baseTypeId;

    // 单选多选选项数
    public String answerNum;

    // 作业或导学案或课堂记录名称
    public String sourceName;

    // 当前习题位置
    public String currentPage;

    // 学生得分
    public String stuScore;

    // 学生答案，为空则表示未答
    public String stuAnswer;

    // 权限控制字段
    public String control;

    // 习题类型
    public String typeName;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    @Override
    public String toString() {
        return "BookRecyclerEntity{" +
                "sourceId='" + sourceId + '\'' +
                ", shitiShow='" + shitiShow + '\'' +
                ", num='" + num + '\'' +
                ", shitiAnswer='" + shitiAnswer + '\'' +
                ", allPage='" + allPage + '\'' +
                ", score='" + score + '\'' +
                ", shitiAnalysis='" + shitiAnalysis + '\'' +
                ", avgScore='" + avgScore + '\'' +
                ", sourceType='" + sourceType + '\'' +
                ", baseTypeId='" + baseTypeId + '\'' +
                ", answerNum='" + answerNum + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", currentPage='" + currentPage + '\'' +
                ", stuScore='" + stuScore + '\'' +
                ", stuAnswer='" + stuAnswer + '\'' +
                ", control='" + control + '\'' +
                ", typeName='" + typeName + '\'' +
                ", questionId='" + questionId + '\'' +
                '}';
    }

    // 习题id
    public String questionId;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getShitiShow() {
        return shitiShow;
    }

    public void setShitiShow(String shitiShow) {
        this.shitiShow = shitiShow;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getShitiAnswer() {
        return shitiAnswer;
    }

    public void setShitiAnswer(String shitiAnswer) {
        this.shitiAnswer = shitiAnswer;
    }

    public String getAllPage() {
        return allPage;
    }

    public void setAllPage(String allPage) {
        this.allPage = allPage;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getShitiAnalysis() {
        return shitiAnalysis;
    }

    public void setShitiAnalysis(String shitiAnalysis) {
        this.shitiAnalysis = shitiAnalysis;
    }

    public String getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(String avgScore) {
        this.avgScore = avgScore;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getBaseTypeId() {
        return baseTypeId;
    }

    public void setBaseTypeId(String baseTypeId) {
        this.baseTypeId = baseTypeId;
    }

    public String getAnswerNum() {
        return answerNum;
    }

    public void setAnswerNum(String answerNum) {
        this.answerNum = answerNum;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public String getStuScore() {
        return stuScore;
    }

    public void setStuScore(String stuScore) {
        this.stuScore = stuScore;
    }

    public String getStuAnswer() {
        return stuAnswer;
    }

    public void setStuAnswer(String stuAnswer) {
        this.stuAnswer = stuAnswer;
    }

    public String getControl() {
        return control;
    }

    public void setControl(String control) {
        this.control = control;
    }

}
