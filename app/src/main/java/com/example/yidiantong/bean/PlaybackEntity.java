package com.example.yidiantong.bean;

import java.io.Serializable;

public class PlaybackEntity implements Serializable {
    private String imageSrc;//照片
    private String showTopTime;//时间
    private String teacherName;//教师姓名
    private String topTitle;//顶部标题

    public String getTopTitle() {
        return topTitle;
    }

    public void setTopTitle(String topTitle) {
        this.topTitle = topTitle;
    }

    @Override
    public String toString() {
        return "PlaybackEntity{" +
                "imageSrc='" + imageSrc + '\'' +
                ", showTopTime='" + showTopTime + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", time='" + time + '\'' +
                ", keciId='" + keciId + '\'' +
                ", showBottomTime='" + showBottomTime + '\'' +
                ", subjectName='" + subjectName + '\'' +
                '}';
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    private String time;
    private String keciId;
    private String showBottomTime;//底部时间
    private String subjectName;

    public PlaybackEntity() {
    }

    public PlaybackEntity(String showTopTime, String teacherName, String time, String keciId, String showBottomTime, String subjectName) {
        this.showTopTime = showTopTime;
        this.teacherName = teacherName;
        this.time = time;
        this.keciId = keciId;
        this.showBottomTime = showBottomTime;
        this.subjectName = subjectName;
    }

    public String getShowTopTime() {
        return showTopTime;
    }

    public void setShowTopTime(String showTopTime) {
        this.showTopTime = showTopTime;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKeciId() {
        return keciId;
    }

    public void setKeciId(String keciId) {
        this.keciId = keciId;
    }

    public String getShowBottomTime() {
        return showBottomTime;
    }

    public void setShowBottomTime(String showBottomTime) {
        this.showBottomTime = showBottomTime;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

}
