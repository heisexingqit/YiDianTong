package com.example.yidiantong.bean;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class LearnPlanAddItemEntity implements Serializable {

    private String filePath;
    private String baseTypeId;
    private String shitiAnalysis;
    private String shitiShow;
    private List<String> pptList;
    private String format;
    private String type;
    private String url;
    private String shitiAnswer;
    private String typeName;
    private String id;
    private String name;
    private String baseTypeName;
    private String typeId;
    private String selectFlag;
    private int order;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getBaseTypeId() {
        return baseTypeId;
    }

    public void setBaseTypeId(String baseTypeId) {
        this.baseTypeId = baseTypeId;
    }

    public String getShitiAnalysis() {
        return shitiAnalysis;
    }

    public void setShitiAnalysis(String shitiAnalysis) {
        this.shitiAnalysis = shitiAnalysis;
    }

    public String getShitiShow() {
        return shitiShow;
    }

    public void setShitiShow(String shitiShow) {
        this.shitiShow = shitiShow;
    }

    public List<String> getPptList() {
        return pptList;
    }

    public void setPptList(List<String> pptList) {
        this.pptList = pptList;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getShitiAnswer() {
        return shitiAnswer;
    }

    public void setShitiAnswer(String shitiAnswer) {
        this.shitiAnswer = shitiAnswer;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBaseTypeName() {
        return baseTypeName;
    }

    public void setBaseTypeName(String baseTypeName) {
        this.baseTypeName = baseTypeName;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getSelectFlag() {
        return selectFlag;
    }

    public void setSelectFlag(String selectFlag) {
        this.selectFlag = selectFlag;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "LearnPlanAddItemEntity{" +
                "filePath='" + filePath + '\'' +
                ", baseTypeId='" + baseTypeId + '\'' +
                ", shitiAnalysis='" + shitiAnalysis + '\'' +
                ", shitiShow='" + shitiShow + '\'' +
                ", pptList=" + pptList +
                ", format='" + format + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", shitiAnswer='" + shitiAnswer + '\'' +
                ", typeName='" + typeName + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", baseTypeName='" + baseTypeName + '\'' +
                ", typeId='" + typeId + '\'' +
                ", selectFlag='" + selectFlag + '\'' +
                '}';
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getPPTListString(){
        return "[" + pptList.stream()
                .map(s -> "\"" + s + "\"")
                .collect(Collectors.joining(", ")) + "]";
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public String toData(){
        return  '{' +
                "\"id\":\"" + id + '\"' +
                ", \"name\":\"" + name + '\"' +
                ", \"type\":\"" + type + '\"' +
                ", \"format\":\"" + format + '\"' +
                ", \"shitiShow\":\"" + shitiShow + '\"' +
                ", \"shitiAnswer\":\"" + shitiAnswer + '\"' +
                ", \"shitiAnalysis\":\"" + shitiAnalysis + '\"' +
                ", \"baseTypeId\":\"" + baseTypeId + '\"' +
                ", \"baseTypeName\":\"" + baseTypeName + '\"' +
                ", \"typeId\":\"" + typeId + '\"' +
                ", \"typeName\":\"" + typeName + '\"' +
                ", \"url\":\"" + url + '\"' +
                ", \"pptList\":" + getPPTListString() +
                ", \"filePath\":\"" + filePath + '\"' +
                ", \"previewPath\":\"" + filePath + '\"' +
                ", \"linkName\":\"" +  + '\"' +
                ", \"linkOrder\":\"" +  + '\"' +
                ", \"activityName\":\"" +  + '\"' +
                ", \"activityOrder\":\"" +  + '\"' +
                ", \"resourceOrder\":\"" + order + '\"' +
                '}';
    }
}
