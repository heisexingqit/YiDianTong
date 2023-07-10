package com.example.yidiantong.bean;

import java.io.Serializable;

public class HomeworkEntity implements Serializable {
    // 题目类型名
    private String questionTypeName;
    // 题目选项列表
    private String questionChoiceList;
    // 题目ID
    private String questionId;
    // 右上角目录显示内容
    private String questionName;
    // 基类ID
    private String baseTypeId;
    // 题面HMTL
    private String questionContent;
    // 题目答案
    private String answer;

    public String getQuestionTypeName() {
        return questionTypeName;
    }

    public void setQuestionTypeName(String questionTypeName) {
        this.questionTypeName = questionTypeName;
    }

    public String getQuestionChoiceList() {
        return questionChoiceList;
    }

    public void setQuestionChoiceList(String questionChoiceList) {
        this.questionChoiceList = questionChoiceList;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public String getBaseTypeId() {
        return baseTypeId;
    }

    public void setBaseTypeId(String baseTypeId) {
        this.baseTypeId = baseTypeId;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "HomeworkEntity{" +
                "questionTypeName='" + questionTypeName + '\'' +
                ", questionChoiceList='" + questionChoiceList + '\'' +
                ", questionId='" + questionId + '\'' +
                ", questionName='" + questionName + '\'' +
                ", baseTypeId='" + baseTypeId + '\'' +
                ", questionContent='" + questionContent + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}
