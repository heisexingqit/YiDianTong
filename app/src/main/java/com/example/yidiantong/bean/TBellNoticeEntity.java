package com.example.yidiantong.bean;

import java.util.List;

public class TBellNoticeEntity {

    // 通知公告内容
    private String content;

    // 作者
    private String author;

    // 标题
    private String title;

    // 总人数
    private int num;

    // 已读人员列表
    private List readList;

    // 已读人员数量
    private int readNum;

    // 未读人员数量
    private int noReadNum;

    // 未读人员列表
    private List noreadList;

    // 是不是作者，true显示修改或撤回按钮
    private Boolean isAuthor;

    // true则修改按钮可点击
    private Boolean isUpdate;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public List getReadList() {
        return readList;
    }

    public void setReadList(List readList) {
        this.readList = readList;
    }

    public Integer getReadNum() {
        return readNum;
    }

    public void setReadNum(Integer readNum) {
        this.readNum = readNum;
    }

    public List getNoreadList() {
        return noreadList;
    }

    public void setNoreadList(List noreadList) {
        this.noreadList = noreadList;
    }

    public Integer getNoReadNum() {
        return noReadNum;
    }

    public void setNoReadNum(Integer noReadNum) {
        this.noReadNum = noReadNum;
    }

    public Boolean getIsAuthor() {
        return isAuthor;
    }

    public void setIsAuthor(Boolean isAuthor) {
        this.isAuthor = isAuthor;
    }

    public Boolean getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(Boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    @Override
    public String toString() {
        return "TBellNoticeEntity{" +
                "content='" + content + '\'' +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", num='" + num + '\'' +
                ", readList='" + readList + '\'' +
                ", readNum='" + readNum + '\'' +
                ", noReadNum='" + noReadNum + '\'' +
                ", noreadList='" + noreadList + '\'' +
                ", isAuthor='" + isAuthor + '\'' +
                ", isUpdate='" + isUpdate + '\'' +
                '}';
    }
}
