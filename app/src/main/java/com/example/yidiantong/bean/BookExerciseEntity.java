package com.example.yidiantong.bean;

public class BookExerciseEntity {


    public String shiTiShow;
    public String shiTiAnalysis;
    public String shiTiAnswer;
    public String typeName;
    public String baseTypeId;
    public String stuAnswer;

    @Override
    public String toString() {
        return "BookExerciseEntity{" +
                "shiTiShow='" + shiTiShow + '\'' +
                ", shiTiAnalysis='" + shiTiAnalysis + '\'' +
                ", shiTiAnswer='" + shiTiAnswer + '\'' +
                ", typeName='" + typeName + '\'' +
                ", baseTypeId='" + baseTypeId + '\'' +
                ", stuAnswer='" + stuAnswer + '\'' +
                '}';
    }
}
