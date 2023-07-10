package com.example.yidiantong.bean;

public class LiveItemEntity {
    private String imgUrl;
    private LiveDate startDate;
    private String time2;
    private String title;
    private String time1;
    private String status;
    private String subject;
    private String roomId;
    private String teacherName;
    private String teacherId;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public LiveDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LiveDate startDate) {
        this.startDate = startDate;
    }

    public String getTime2() {
        return time2;
    }

    public void setTime2(String time2) {
        this.time2 = time2;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime1() {
        return time1;
    }

    public void setTime1(String time1) {
        this.time1 = time1;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    @Override
    public String toString() {
        return "LiveItemEntity{" +
                "imgUrl='" + imgUrl + '\'' +
                ", startDate='" + startDate + '\'' +
                ", time2='" + time2 + '\'' +
                ", title='" + title + '\'' +
                ", time1='" + time1 + '\'' +
                ", status='" + status + '\'' +
                ", subject='" + subject + '\'' +
                ", roomId='" + roomId + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", teacherId='" + teacherId + '\'' +
                '}';
    }
}
