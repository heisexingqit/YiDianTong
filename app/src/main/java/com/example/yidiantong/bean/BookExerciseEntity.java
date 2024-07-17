package com.example.yidiantong.bean;

import java.io.Serializable;

public class BookExerciseEntity implements Serializable {
    public String shiTiShow; // 题面
    public String shiTiAnalysis; // 解析
    public String shiTiAnswer; // 标准答案
    public String typeName; // 题目标题
    public String baseTypeId; // 题目基类
    public String stuAnswer = ""; // 学生作答
    public int orderNum; // 小题个数
    public String stuHtml = ""; // 主观题图片url
    public String questionId; // 题目id
    public int answerNumber;//答案个数
    public String questionKeyword;//试题考点
    public String score;//试题分数
    public int accType = 0;//作答是否正确类型  1正确 2错误 3部分正确  也用于提交时判断是否有作答
    public String stuInput;//学生作答信息
    public String stuScore;//学生作答分数
    public Boolean isZuoDaMeiPingFen = false;//是否评分
    public String zuodaDate;//学生作答时间

    public String getZuodaDate() {
        return zuodaDate;
    }

    public void setZuodaDate(String zuodaDate) {
        this.zuodaDate = zuodaDate;
    }

    public String getStuScore() {
        return stuScore;
    }

    public void setStuScore(String stuScore) {
        this.stuScore = stuScore;
    }

    public Boolean getIsZuoDaMeiPingFen() {
        return isZuoDaMeiPingFen;
    }

    public void setIsZuoDaMeiPingFen(Boolean isZuoDaMeiPingFen) {
        this.isZuoDaMeiPingFen = isZuoDaMeiPingFen;
    }

    public String getStuInput() {
        return stuInput;
    }

    public void setStuInput(String stuInput) {
        this.stuInput = stuInput;
    }

    public int getAccType() {
        return accType;
    }

    public void setAccType(int accType) {
        this.accType = accType;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getQuestionKeyword() {
        return questionKeyword;
    }

    public void setQuestionKeyword(String questionKeyword) {
        this.questionKeyword = questionKeyword;
    }

    public int getAnswerNumber() {
        return answerNumber;
    }

    public void setAnswerNumber(int answerNumber) {
        this.answerNumber = answerNumber;
    }

    public String getQuestionsSource() {
        return questionsSource;
    }

    public void setQuestionsSource(String questionsSource) {
        this.questionsSource = questionsSource;
    }

    public String questionsSource;//知识点标题

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getShiTiShow() {
        return shiTiShow;
    }

    public void setShiTiShow(String shiTiShow) {
        this.shiTiShow = shiTiShow;
    }

    public String getShiTiAnalysis() {
        return shiTiAnalysis;
    }

    public void setShiTiAnalysis(String shiTiAnalysis) {
        this.shiTiAnalysis = shiTiAnalysis;
    }

    public String getShiTiAnswer() {
        return shiTiAnswer;
    }

    public void setShiTiAnswer(String shiTiAnswer) {
        this.shiTiAnswer = shiTiAnswer;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getBaseTypeId() {
        return baseTypeId;
    }

    public void setBaseTypeId(String baseTypeId) {
        this.baseTypeId = baseTypeId;
    }

    public String getStuAnswer() {
        return stuAnswer;
    }

    public void setStuAnswer(String stuAnswer) {
        this.stuAnswer = stuAnswer;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public String getStuHtml() {
        return stuHtml;
    }

    public void setStuHtml(String stuHtml) {
        this.stuHtml = stuHtml;
    }

    @Override
    public String toString() {
        return "BookExerciseEntity{" +
                "questionKeyword='" + questionKeyword + '\'' +
                ", shiTiAnswer='" + shiTiAnswer + '\'' +
                ", typeName='" + typeName + '\'' +
                ", baseTypeId='" + baseTypeId + '\'' +
                ", stuAnswer='" + stuAnswer + '\'' +
                ", orderNum=" + orderNum +
                ", stuHtml='" + stuHtml + '\'' +
                ", questionId='" + questionId + '\'' +
                ", answerNumber='" + answerNumber + '\'' +
                ", questionsSource='" + questionsSource + '\'' +
                ", shiTiAnalysis='" + shiTiAnalysis + '\'' +
                ", shiTiShow='" + shiTiShow + '\'' +
                '}';
    }
}
