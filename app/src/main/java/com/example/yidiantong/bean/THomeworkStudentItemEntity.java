package com.example.yidiantong.bean;

import java.io.Serializable;

public class THomeworkStudentItemEntity implements Serializable {
    private String teaPreview;
    private String time;
    private String paperId;
    private String scoreCount;
    private String status;
    private String optionTimeStr;
    private String score;
    private String userPhoto;
    private String userName;
    private String userCn;
    private String description;

    public String getTeaPreview() {
        return teaPreview;
    }

    public void setTeaPreview(String teaPreview) {
        this.teaPreview = teaPreview;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPaperId() {
        return paperId;
    }

    public void setPaperId(String paperId) {
        this.paperId = paperId;
    }

    public String getScoreCount() {
        return scoreCount;
    }

    public void setScoreCount(String scoreCount) {
        this.scoreCount = scoreCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOptionTimeStr() {
        return optionTimeStr;
    }

    public void setOptionTimeStr(String optionTimeStr) {
        this.optionTimeStr = optionTimeStr;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCn() {
        return userCn;
    }

    public void setUserCn(String userCn) {
        this.userCn = userCn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "THomeworkStudentItemEntity{" +
                "teaPreview='" + teaPreview + '\'' +
                ", time='" + time + '\'' +
                ", paperId='" + paperId + '\'' +
                ", scoreCount='" + scoreCount + '\'' +
                ", status='" + status + '\'' +
                ", optionTimeStr='" + optionTimeStr + '\'' +
                ", score='" + score + '\'' +
                ", userPhoto='" + userPhoto + '\'' +
                ", userName='" + userName + '\'' +
                ", userCn='" + userCn + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
