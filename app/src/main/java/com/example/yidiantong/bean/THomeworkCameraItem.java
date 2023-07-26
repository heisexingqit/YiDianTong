package com.example.yidiantong.bean;

public class THomeworkCameraItem {
    private String questionId;
    private String shitiShow;
    private String shitiAnswer;
    private String shitiAnalysis;
    private String baseTypeId;
    private String typeId;
    private String typeName;
    private String score;
    private String order;
    private String answerNum;
    private String smallQueNum;
    private boolean isShow;

    public THomeworkCameraItem() {
        answerNum = "-1";
        smallQueNum = "-1";
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getShitiShow() {
        return shitiShow;
    }

    public void setShitiShow(String shitiShow) {
        this.shitiShow = shitiShow;
    }

    public String getShitiAnswer() {
        return shitiAnswer;
    }

    public void setShitiAnswer(String shitiAnswer) {
        this.shitiAnswer = shitiAnswer;
    }

    public String getShitiAnalysis() {
        return shitiAnalysis;
    }

    public void setShitiAnalysis(String shitiAnalysis) {
        this.shitiAnalysis = shitiAnalysis;
    }

    public String getBaseTypeId() {
        return baseTypeId;
    }

    public void setBaseTypeId(String baseTypeId) {
        this.baseTypeId = baseTypeId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getAnswerNum() {
        return answerNum;
    }

    public void setAnswerNum(String answerNum) {
        this.answerNum = answerNum;
    }

    public String getSmallQueNum() {
        return smallQueNum;
    }

    public void setSmallQueNum(String smallQueNum) {
        this.smallQueNum = smallQueNum;
    }


    @Override
    public String toString() {
        return "THomeworkCameraItem{" +
                "questionId='" + questionId + '\'' +
                ", shitiShow='" + shitiShow + '\'' +
                ", shitiAnswer='" + shitiAnswer + '\'' +
                ", shitiAnalysis='" + shitiAnalysis + '\'' +
                ", baseTypeId='" + baseTypeId + '\'' +
                ", typeId='" + typeId + '\'' +
                ", typeName='" + typeName + '\'' +
                ", score='" + score + '\'' +
                ", order='" + order + '\'' +
                ", answerNum='" + answerNum + '\'' +
                ", smallQueNum='" + smallQueNum + '\'' +
                ", isShow=" + isShow +
                '}';
    }

    public String toData() {
        String str = shitiAnalysis;
        if(str == null || str.length() == 0){
            str = "略";
        }
        return "{" +
                "questionId=\"" + questionId + '\"' +
                ", shitiShow=\"" + shitiShow.replaceAll("[\"']", "\\\\$0") + '\"' +
                ", shitiAnswer=\"" + shitiAnswer.replaceAll("[\"']", "\\\\$0") + '\"' +
                ", shitiAnalysis=\"" + str.replaceAll("[\"']", "\\\\$0") + '\"' +
                ", baseTypeId=\"" + baseTypeId + '\"' +
                ", typeId=\"" + typeId + '\"' +
                ", typeName=\"" + typeName + '\"' +
                ", score=\"" + score + '\"' +
                ", order=\"" + order + '\"' +
                ", answerNum=\"" + answerNum + '\"' +
                ", smallQueNum=\"" + smallQueNum + '\"' +
                '}';
    }
}
