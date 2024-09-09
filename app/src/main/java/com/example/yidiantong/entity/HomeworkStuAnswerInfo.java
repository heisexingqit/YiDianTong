package com.example.yidiantong.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(primaryKeys = {"userId", "homeworkId", "questionId"})
public class HomeworkStuAnswerInfo {
    @NonNull
    public String userId; // 学生id
    @NonNull
    public String homeworkId; // 作业id
    @NonNull
    public String questionId; // 试题id
    public String stuAnswer; // 学生作答
    public String userName; // 学生名称
    public int order; // 题目顺序
    public String updateDate; // 更新日期

    public String answerTime; // 作答时间
    public String useTime; // 页面用时
    public String type;


    public HomeworkStuAnswerInfo() {
        this.answerTime = "";
        this.useTime = "0";
        type = "que";
    }

    @Ignore
    public HomeworkStuAnswerInfo(@NonNull String userId, @NonNull String homeworkId, @NonNull String questionId, String stuAnswer, String userName, int order, String updateDate) {
        this.userId = userId;
        this.homeworkId = homeworkId;
        this.questionId = questionId;
        this.stuAnswer = stuAnswer;
        this.userName = userName;
        this.order = order;
        this.updateDate = updateDate;
        this.answerTime = "";
        this.useTime = "0";
        this.type = "que";
    }

    @Override
    public String toString() {
        return "HomeworkStuAnswerInfo{" +
                "userId='" + userId + '\'' +
                ", homeworkId='" + homeworkId + '\'' +
                ", questionId='" + questionId + '\'' +
                ", stuAnswer='" + stuAnswer + '\'' +
                ", userName='" + userName + '\'' +
                ", updateDate='" + updateDate + '\'' +
                '}';
    }
}
