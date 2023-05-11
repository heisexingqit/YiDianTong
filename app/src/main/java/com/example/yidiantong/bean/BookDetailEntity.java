package com.example.yidiantong.bean;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class BookDetailEntity {

    // 资源类型（作业、导学案等）
    public String sourceType;

    // 知识点ID
    public String sourceId;

    // 知识点名称
    public String sourceName;

    // 错题数量
    public String errorQueNum;

    // 错题列表
    public List<errorList> list;


    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getErrorQueNum() {
        return errorQueNum;
    }

    public void setErrorQueNum(String errorQueNum) {
        this.errorQueNum = errorQueNum;
    }

    public List<errorList> getList() {
        return list;
    }

    public void setList(List<errorList> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "BookDetailEntity{" +
                "sourceType='" + sourceType + '\'' +
                ", sourceId='" + sourceId + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", errorQueNum='" + errorQueNum + '\'' +
                ", list=" + list +
                '}';
    }

    public class errorList{
        // 题目分数
        public String score;

        // 视频图标显示
        public String mp4Flag;

        // 错题Id
        public String questionId;

        // 错题内容
        public String shitiShow;

        // 错题平均分
        public String avgScore;

        // 错题学生分数
        public String stuScore;

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getMp4Flag() {
            return mp4Flag;
        }

        public void setMp4Flag(String mp4Flag) {
            this.mp4Flag = mp4Flag;
        }

        public String getQuestionId() {
            return questionId;
        }

        public void setQuestionId(String questionId) {
            this.questionId = questionId;
        }

        public String getShitiShow() {
            return shitiShow;
        }

        public void setShitiShow(String shitiShow) {
            this.shitiShow = shitiShow;
        }

        public String getAvgScore() {
            return avgScore;
        }

        public void setAvgScore(String avgScore) {
            this.avgScore = avgScore;
        }

        public String getStuScore() {
            return stuScore;
        }

        public void setStuScore(String stuScore) {
            this.stuScore = stuScore;
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        // 错题题号
        public String num;

        @Override
        public String toString() {
            return "errorList{" +
                    "score='" + score + '\'' +
                    ", mp4Flag='" + mp4Flag + '\'' +
                    ", questionId='" + questionId + '\'' +
                    ", shitiShow='" + shitiShow + '\'' +
                    ", avgScore='" + avgScore + '\'' +
                    ", stuScore='" + stuScore + '\'' +
                    ", num='" + num + '\'' +
                    '}';
        }
    }


}
