package com.example.yidiantong.bean;

import java.util.List;

public class TBellUpdateEntity {
    List<TBellAnnounceUpdateEntity> TBellAnnounceUpdateEntity;
    List<TBellNoticeUpdateEntity> TBellNoticeUpdateEntity;
    //List<TBellNoticeUpdateEntity> noticelist;

    public class TBellAnnounceUpdateEntity{
        // 内容
        public String content;
        // 定时时间
        public String setDate;
        // 题目
        public String title;
        // 选择范围，0全部，1老师，2学生
        public Integer type;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getSetDate() {
            return setDate;
        }

        public void setSetDate(String setDate) {
            this.setDate = setDate;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "TBellAnnounceUpdateEntity{" +
                    "content='" + content + '\'' +
                    ", setDate='" + setDate + '\'' +
                    ", title='" + title + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }


    public class TBellNoticeUpdateEntity{
        // 内容
        public String content;
        // 定时时间
        public String setDate;
        // 题目
        public String title;
        // 班级编号
        public String classId;
        // 班级名称
        public String className;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getSetDate() {
            return setDate;
        }

        public void setSetDate(String setDate) {
            this.setDate = setDate;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getClassId() {
            return classId;
        }

        public void setClassId(String classId) {
            this.classId = classId;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        @Override
        public String toString() {
            return "TBellNoticeUpdateEntity{" +
                    "content='" + content + '\'' +
                    ", setDate='" + setDate + '\'' +
                    ", title='" + title + '\'' +
                    ", classId='" + classId + '\'' +
                    ", className='" + className + '\'' +
                    '}';
        }
    }

}
