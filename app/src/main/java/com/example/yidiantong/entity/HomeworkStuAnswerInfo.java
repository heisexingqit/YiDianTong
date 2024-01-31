package com.example.yidiantong.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
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
