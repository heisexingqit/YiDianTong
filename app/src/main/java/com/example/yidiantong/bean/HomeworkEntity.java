package com.example.yidiantong.bean;

public class HomeworkEntity {
    private String questionContent;
    private String questionTypeName;
    private String questionChoiceList;
    private String questionId;
    private String answer;
    private String questionName;

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    @Override
    public String toString() {
        return "HomeworkEntity{" +
                "questionContent='" + questionContent + '\'' +
                ", questionTypeName='" + questionTypeName + '\'' +
                ", questionChoiceList='" + questionChoiceList + '\'' +
                ", questionId='" + questionId + '\'' +
                ", answer='" + answer + '\'' +
                ", questionName='" + questionName + '\'' +
                '}';
    }
}
