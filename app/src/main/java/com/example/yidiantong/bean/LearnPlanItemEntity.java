package com.example.yidiantong.bean;

import java.io.Serializable;
import java.util.List;

public class LearnPlanItemEntity implements Serializable {

    private String resourceId;
    private String questionChoiceList;
    private String baseTypeId;
    private String answer;
    private String path;
    private List<String> pptList;
    private String format;
    private String question;
    private String resourceName;
    private String url;
    private String resourceType;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getQuestionChoiceList() {
        return questionChoiceList;
    }

    public void setQuestionChoiceList(String questionChoiceList) {
        this.questionChoiceList = questionChoiceList;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getPptList() {
        return pptList;
    }

    public void setPptList(List<String> pptList) {
        this.pptList = pptList;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public String toString() {
        return "LearnPlanItemEntity{" +
                "resourceId='" + resourceId + '\'' +
                ", questionChoiceList='" + questionChoiceList + '\'' +
                ", baseTypeId='" + baseTypeId + '\'' +
                ", answer='" + answer + '\'' +
                ", path='" + path + '\'' +
                ", pptList=" + pptList +
                ", format='" + format + '\'' +
                ", question='" + question + '\'' +
                ", resourceName='" + resourceName + '\'' +
                ", url='" + url + '\'' +
                ", resourceType='" + resourceType + '\'' +
                '}';
    }
}
