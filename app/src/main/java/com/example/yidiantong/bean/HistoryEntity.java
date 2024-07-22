package com.example.yidiantong.bean;

import java.io.Serializable;

public class HistoryEntity implements Serializable {
    public String qNum; // 试题总数
    public String answerQueNum; // 已答试题
    public String dbid; // 已答试题
    public String name; // 已答试题
    public String id; // 已答试题
    public String createDateStr; // 已答试题
    public String pointNum; // 考点总数
    public String subjectName; // 学科名称

    public String getQNum() {
        return qNum;
    }

    public void setQNum(String qNum) {
        this.qNum = qNum;
    }

    public String getAnswerQueNum() {
        return answerQueNum;
    }

    public void setAnswerQueNum(String answerQueNum) {
        this.answerQueNum = answerQueNum;
    }

    public String getDbid() {
        return dbid;
    }

    public void setDbid(String dbid) {
        this.dbid = dbid;
    }

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

    public String getCreateDateStr() {
        return createDateStr;
    }

    public void setCreateDateStr(String createDateStr) {
        this.createDateStr = createDateStr;
    }

    public String getPointNum() {
        return pointNum;
    }

    public void setPointNum(String pointNum) {
        this.pointNum = pointNum;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    @Override
    public String toString() {
        return "HistoryEntity{" +
                "qNum='" + qNum + '\'' +
                ", answerQueNum='" + answerQueNum + '\'' +
                ", dbid='" + dbid + '\'' +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", createDateStr='" + createDateStr + '\'' +
                ", pointNum='" + pointNum + '\'' +
                ", subjectName='" + subjectName + '\'' +
                '}';
    }
}
