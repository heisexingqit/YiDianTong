package com.example.yidiantong.bean;

import java.util.List;
public class TBellNoticeEntity_New {

    // 通知公告内容
    private String content;

    // 作者
    private String author;

    // 标题
    private String title;

    // 学生总人数
    private int stuNum;

    //学生已读总数
    private int stureadNum;

    //学生未读总数
    private int stunoreadNum;

    //学生已读名单
    private List stureadList;

    //学生未读名单
    private List stunoreadList;

    //教师总人数
    private int teanum;

    //教师已读总数
    private int teareadnum;

    // 教师未读总数
    private int teanoreadnum;

    //教师已读名单
    private List teareadList;

    //教师未读名单
    private List teanoreadList;


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

    public boolean getIsAuthor() {
        return isAuthor;
    }

    public Boolean getIsUpdate() {
        return isUpdate;
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

    public int getStuNum() {
        return stuNum;
    }

    public void setStuNum(int stuNum) {
        this.stuNum = stuNum;
    }

    public int getStureadNum() {
        return stureadNum;
    }

    public void setStureadNum(int stureadNum) {
        this.stureadNum = stureadNum;
    }

    public int getStunoreadNum() {
        return stunoreadNum;
    }

    public void setStunoreadNum(int stunoreadNum) {
        this.stunoreadNum = stunoreadNum;
    }

    public List getStureadList() {
        return stureadList;
    }

    public void setStureadList(List stureadList) {
        this.stureadList = stureadList;
    }

    public List getStunoreadList() {
        return stunoreadList;
    }

    public void setStunoreadList(List stunoreadList) {
        this.stunoreadList = stunoreadList;
    }

    public int getTeanum() {
        return teanum;
    }

    public void setTeanum(int teanum) {
        this.teanum = teanum;
    }

    public int getTeareadnum() {
        return teareadnum;
    }

    public void setTeareadnum(int teareadnum) {
        this.teareadnum = teareadnum;
    }

    public int getTeanoreadnum() {
        return teanoreadnum;
    }

    public void setTeanoreadnum(int teanoreadnum) {
        this.teanoreadnum = teanoreadnum;
    }

    public List getTeareadList() {
        return teareadList;
    }

    public void setTeareadList(List teareadList) {
        this.teareadList = teareadList;
    }

    public List getTeanoreadList() {
        return teanoreadList;
    }

    public void setTeanoreadList(List teanoreadList) {
        this.teanoreadList = teanoreadList;
    }

    @Override
    public String toString() {
        return "TBellNoticeEntity_New{" +
                "content='" + content + '\'' +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", stuNum=" + stuNum +
                ", stureadNum=" + stureadNum +
                ", stunoreadNum=" + stunoreadNum +
                ", stureadList=" + stureadList +
                ", stunoreadList=" + stunoreadList +
                ", teanum=" + teanum +
                ", teareadnum=" + teareadnum +
                ", teanoreadnum=" + teanoreadnum +
                ", teareadList=" + teareadList +
                ", teanoreadList=" + teanoreadList +
                ", isAuthor=" + isAuthor +
                ", isUpdate=" + isUpdate +
                '}';
    }
}
