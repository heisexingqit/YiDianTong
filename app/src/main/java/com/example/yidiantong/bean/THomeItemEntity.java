package com.example.yidiantong.bean;

public class THomeItemEntity {

    private String fFlag;
    private String fNumber;
    private String fTime;
    private String fId;
    private String fImg;
    private String fName;
    private String fDescription;
    private String fNum1; //未阅
    private String fNum2; //已阅
    private String fNum3; //未交
    private String fNum4; //属性完成度
    private String fNum5; //资源总数
    private String fType; //1导学案，2作业，3通知，4公告，6授课包，7微课

    public String getfFlag() {
        return fFlag;
    }

    public void setfFlag(String fFlag) {
        this.fFlag = fFlag;
    }

    public String getfNumber() {
        return fNumber;
    }

    public void setfNumber(String fNumber) {
        this.fNumber = fNumber;
    }

    public String getfTime() {
        return fTime;
    }

    public void setfTime(String fTime) {
        this.fTime = fTime;
    }

    public String getfId() {
        return fId;
    }

    public void setfId(String fId) {
        this.fId = fId;
    }

    public String getfImg() {
        return fImg;
    }

    public void setfImg(String fImg) {
        this.fImg = fImg;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getfDescription() {
        return fDescription;
    }

    public void setfDescription(String fDescription) {
        this.fDescription = fDescription;
    }

    public String getfNum1() {
        return fNum1;
    }

    public void setfNum1(String fNum1) {
        this.fNum1 = fNum1;
    }

    public String getfNum2() {
        return fNum2;
    }

    public void setfNum2(String fNum2) {
        this.fNum2 = fNum2;
    }

    public String getfNum3() {
        return fNum3;
    }

    public void setfNum3(String fNum3) {
        this.fNum3 = fNum3;
    }

    public String getfNum4() {
        return fNum4;
    }

    public void setfNum4(String fNum4) {
        this.fNum4 = fNum4;
    }

    public String getfNum5() {
        return fNum5;
    }

    public void setfNum5(String fNum5) {
        this.fNum5 = fNum5;
    }

    public String getfType() {
        return fType;
    }

    public void setfType(String fType) {
        this.fType = fType;
    }

    @Override
    public String toString() {
        return "THomeItemEntity{" +
                "fFlag='" + fFlag + '\'' +
                ", fNumber='" + fNumber + '\'' +
                ", fTime='" + fTime + '\'' +
                ", fId='" + fId + '\'' +
                ", fImg='" + fImg + '\'' +
                ", fName='" + fName + '\'' +
                ", fDescription='" + fDescription + '\'' +
                ", fNum1='" + fNum1 + '\'' +
                ", fNum2='" + fNum2 + '\'' +
                ", fNum3='" + fNum3 + '\'' +
                ", fNum4='" + fNum4 + '\'' +
                ", fNum5='" + fNum5 + '\'' +
                ", fType='" + fType + '\'' +
                '}';
    }
}
