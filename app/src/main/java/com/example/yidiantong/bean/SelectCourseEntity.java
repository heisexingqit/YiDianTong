package com.example.yidiantong.bean;

public class SelectCourseEntity {
    // 模式 1：3+1+2；2：六选三；3：七选三
    public String mode;
    // 已选科目：“未选科”
    public String subjectComposeName;
    // 结束时间
    public String endTimeStr;
    // 开始时间
    public String startTimeStr;
    // 大标题
    public String taskName;
    // 类型 1：正式选；2：模拟选
    public String type;
    // 项目id
    public String taskId;
    // 状态 1：未发布 2：已发布 3：已暂停 4：已截止
    public String status;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getSubjectComposeName() {
        return subjectComposeName;
    }

    public void setSubjectComposeName(String subjectComposeName) {
        this.subjectComposeName = subjectComposeName;
    }

    public String getEndTimeStr() {
        return endTimeStr;
    }

    public void setEndTimeStr(String endTimeStr) {
        this.endTimeStr = endTimeStr;
    }

    public String getStartTimeStr() {
        return startTimeStr;
    }

    public void setStartTimeStr(String startTimeStr) {
        this.startTimeStr = startTimeStr;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SelectCourseEntity{" +
                "mode='" + mode + '\'' +
                ", subjectComposeName='" + subjectComposeName + '\'' +
                ", endTimeStr='" + endTimeStr + '\'' +
                ", startTimeStr='" + startTimeStr + '\'' +
                ", taskName='" + taskName + '\'' +
                ", type='" + type + '\'' +
                ", taskId='" + taskId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
