package com.example.yidiantong.bean;

import android.util.Log;

import com.example.yidiantong.R;

import java.util.ArrayList;

public class BookInfoEntity {

    // 课程名称
    public String subjectName;

    // 是否已读
    public String status;

    // 错题数量
    public String errorQueNum;

    @Override
    public String toString() {
        return "BookInfoEntity{" +
                "subjectName='" + subjectName + '\'' +
                ", status='" + status + '\'' +
                ", errorQueNum='" + errorQueNum + '\'' +
                ", subjectId='" + subjectId + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    // 学科Id
    public String subjectId;

    // 图片
    public String image;


}
