package com.example.yidiantong.bean;

public class CourseScannerEntity {
    public String id;
    public String imgSource;
    public String learnPlanName;
    public String teacher;
    public String introduction;
    public String adIds;
    public String courseName;
    public String className;
    public String teacherName;
    public String subject;
    public String lpVersion;
    public String segment;
    public String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgSource() {
        return imgSource;
    }

    public void setImgSource(String imgSource) {
        this.imgSource = imgSource;
    }

    public String getLearnPlanName() {
        return learnPlanName;
    }

    public void setLearnPlanName(String learnPlanName) {
        this.learnPlanName = learnPlanName;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getAdIds() {
        return adIds;
    }

    public void setAdIds(String adIds) {
        this.adIds = adIds;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getLpVersion() {
        return lpVersion;
    }

    public void setLpVersion(String lpVersion) {
        this.lpVersion = lpVersion;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CourseScannerEntity{" +
                "id='" + id + '\'' +
                ", imgSource='" + imgSource + '\'' +
                ", learnPlanName='" + learnPlanName + '\'' +
                ", teacher='" + teacher + '\'' +
                ", introduction='" + introduction + '\'' +
                ", adIds='" + adIds + '\'' +
                ", courseName='" + courseName + '\'' +
                ", className='" + className + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", subject='" + subject + '\'' +
                ", lpVersion='" + lpVersion + '\'' +
                ", segment='" + segment + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
