package com.example.yidiantong.bean;

public class BookExerciseEntity {
    public String shiTiShow; // 题面
    public String shiTiAnalysis; // 解析
    public String shiTiAnswer; // 标准答案
    public String typeName; // 题目标题
    public String baseTypeId; // 题目基类
    public String stuAnswer; // 学生作答
    public int orderNum; // 小题个数
    public String stuHtml = ""; // 主观题图片url
    @Override
    public String toString() {
        return "BookExerciseEntity{" +
                ", shiTiAnswer='" + shiTiAnswer + '\'' +
                ", typeName='" + typeName + '\'' +
                ", baseTypeId='" + baseTypeId + '\'' +
                ", stuAnswer='" + stuAnswer + '\'' +
                ", orderNum=" + orderNum +
                '}';
    }
}
