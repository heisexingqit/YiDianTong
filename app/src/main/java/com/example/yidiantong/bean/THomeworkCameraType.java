package com.example.yidiantong.bean;

public class THomeworkCameraType {
    private String score;
    private String typeName;
    private String baseTypeId;
    private String typeId;

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getBaseTypeId() {
        return baseTypeId;
    }

    public void setBaseTypeId(String baseTypeId) {
        this.baseTypeId = baseTypeId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    @Override
    public String toString() {
        return "HomeworkCameraType{" +
                "score='" + score + '\'' +
                ", typeName='" + typeName + '\'' +
                ", baseTypeId='" + baseTypeId + '\'' +
                ", typeId='" + typeId + '\'' +
                '}';
    }
}
