package com.example.yidiantong.bean;

import java.util.List;

public class SelectDetailEntity {
    // 提示，null或空则不显示
    public String des;
    // 截止时间
    public String endTimeStr;

    public String composeName;
    // 开始时间
    public String startTimeStr;
    // 大标题
    public String taskName;
    // 选科id，为空则表明该生未选科，不为0则对应到list中的id
    public String composeId;
    // 全部选科列表
    public List<courseList> list;

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getEndTimeStr() {
        return endTimeStr;
    }

    public void setEndTimeStr(String endTimeStr) {
        this.endTimeStr = endTimeStr;
    }

    public String getComposeName() {
        return composeName;
    }

    public void setComposeName(String composeName) {
        this.composeName = composeName;
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

    public String getComposeId() {
        return composeId;
    }

    public void setComposeId(String composeId) {
        this.composeId = composeId;
    }

    public List<courseList> getList() {
        return list;
    }

    public void setList(List<courseList> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "SelectDetailEntity{" +
                "des='" + des + '\'' +
                ", endTimeStr='" + endTimeStr + '\'' +
                ", composeName='" + composeName + '\'' +
                ", startTimeStr='" + startTimeStr + '\'' +
                ", taskName='" + taskName + '\'' +
                ", composeId='" + composeId + '\'' +
                '}';
    }

    public class courseList{
        // 选科名称
        public String name;
        // 选科id
        public String id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "courseList{" +
                    "name='" + name + '\'' +
                    ", id='" + id + '\'' +
                    '}';
        }
    }
}


