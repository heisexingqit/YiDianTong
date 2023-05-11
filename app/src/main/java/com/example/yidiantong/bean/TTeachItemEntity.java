package com.example.yidiantong.bean;

public class TTeachItemEntity {
    private String paperType;
    private String propertyCount;
    private String resCount;
    private String createTime;
    private String name;
    private String iconUrl;
    private String id;

    private String channelCode;
    private String channel;

    private String subjectId;
    private String subject;

    private String textBookId;
    private String textBook;

    private String gradeBookCode;
    private String gradeBook;

    private String knowledgeCode;
    private String knowledge;

    private String description;

    public String getPaperType() {
        return paperType;
    }

    public void setPaperType(String paperType) {
        this.paperType = paperType;
    }

    public String getPropertyCount() {
        return propertyCount;
    }

    public void setPropertyCount(String propertyCount) {
        this.propertyCount = propertyCount;
    }

    public String getResCount() {
        return resCount;
    }

    public void setResCount(String resCount) {
        this.resCount = resCount;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTextBookId() {
        return textBookId;
    }

    public void setTextBookId(String textBookId) {
        this.textBookId = textBookId;
    }

    public String getTextBook() {
        return textBook;
    }

    public void setTextBook(String textBook) {
        this.textBook = textBook;
    }

    public String getGradeBookCode() {
        return gradeBookCode;
    }

    public void setGradeBookCode(String gradeBookCode) {
        this.gradeBookCode = gradeBookCode;
    }

    public String getGradeBook() {
        return gradeBook;
    }

    public void setGradeBook(String gradeBook) {
        this.gradeBook = gradeBook;
    }

    public String getKnowledgeCode() {
        return knowledgeCode;
    }

    public void setKnowledgeCode(String knowledgeCode) {
        this.knowledgeCode = knowledgeCode;
    }

    public String getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(String knowledge) {
        this.knowledge = knowledge;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "TTeachItemEntity{" +
                "paperType='" + paperType + '\'' +
                ", propertyCount='" + propertyCount + '\'' +
                ", resCount='" + resCount + '\'' +
                ", createTime='" + createTime + '\'' +
                ", name='" + name + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", id='" + id + '\'' +
                ", channelCode='" + channelCode + '\'' +
                ", channel='" + channel + '\'' +
                ", subjectId='" + subjectId + '\'' +
                ", subject='" + subject + '\'' +
                ", textBookId='" + textBookId + '\'' +
                ", textBook='" + textBook + '\'' +
                ", gradeBookCode='" + gradeBookCode + '\'' +
                ", gradeBook='" + gradeBook + '\'' +
                ", knowledgeCode='" + knowledgeCode + '\'' +
                ", knowledge='" + knowledge + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
