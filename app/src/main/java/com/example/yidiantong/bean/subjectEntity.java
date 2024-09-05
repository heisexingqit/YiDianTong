package com.example.yidiantong.bean;

public class subjectEntity {
    private String image;//学科
    private String errorQueNum;
    private String subjectId;
    private String status;
    private String subjectName;

    public subjectEntity() {
    }

    public subjectEntity(String image, String errorQueNum, String subjectId, String status, String subjectName) {
        this.image = image;
        this.errorQueNum = errorQueNum;
        this.subjectId = subjectId;
        this.status = status;
        this.subjectName = subjectName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getErrorQueNum() {
        return errorQueNum;
    }

    public void setErrorQueNum(String errorQueNum) {
        this.errorQueNum = errorQueNum;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    @Override
    public String toString() {
        return "subjectEntity{" +
                "image='" + image + '\'' +
                ", errorQueNum='" + errorQueNum + '\'' +
                ", subjectId='" + subjectId + '\'' +
                ", status='" + status + '\'' +
                ", subjectName='" + subjectName + '\'' +
                '}';
    }
}
