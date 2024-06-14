package com.example.yidiantong.bean;

import java.util.List;

public class THomeworkReportEntity {
    private String avg;        //平均分
    private String max;        //最高分
    private String min;        //最低分
    private String noSubmit;        //未提交人数
    private String noCorrecting;    //未批改人数
    private String correcting;        //已批改人数
    private List<String> maxList;//最高分人员列表
    private List<String> minList;//最高分人员列表
    private List<String> noSubmitList;//未提交人员列表
    private List<String> noCorrectingList;//未批改人员列表
    private List<String> correctingList;//已批改人员列表

    public String getAvg() {
        return avg;
    }

    public void setAvg(String avg) {
        this.avg = avg;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getNoSubmit() {
        return noSubmit;
    }

    public void setNoSubmit(String noSubmit) {
        this.noSubmit = noSubmit;
    }

    public String getNoCorrecting() {
        return noCorrecting;
    }

    public void setNoCorrecting(String noCorrecting) {
        this.noCorrecting = noCorrecting;
    }

    public String getCorrecting() {
        return correcting;
    }

    public void setCorrecting(String correcting) {
        this.correcting = correcting;
    }

    public List<String> getMaxList() {
        return maxList;
    }

    public void setMaxList(List<String> maxList) {
        this.maxList = maxList;
    }

    public List<String> getMinList() {
        return minList;
    }

    public void setMinList(List<String> minList) {
        this.minList = minList;
    }

    public List<String> getNoSubmitList() {
        return noSubmitList;
    }

    public void setNoSubmitList(List<String> noSubmitList) {
        this.noSubmitList = noSubmitList;
    }

    public List<String> getNoCorrectingList() {
        return noCorrectingList;
    }

    public void setNoCorrectingList(List<String> noCorrectingList) {
        this.noCorrectingList = noCorrectingList;
    }

    public List<String> getCorrectingList() {
        return correctingList;
    }

    public void setCorrectingList(List<String> correctingList) {
        this.correctingList = correctingList;
    }

    @Override
    public String toString() {
        return "THomeworkReportEntity{" +
                "avg='" + avg + '\'' +
                ", max='" + max + '\'' +
                ", min='" + min + '\'' +
                ", noSubmit='" + noSubmit + '\'' +
                ", noCorrecting='" + noCorrecting + '\'' +
                ", correcting='" + correcting + '\'' +
                ", maxList=" + maxList +
                ", minList=" + minList +
                ", noSubmitList=" + noSubmitList +
                ", noCorrectingList=" + noCorrectingList +
                ", correctingList=" + correctingList +
                '}';
    }
}
