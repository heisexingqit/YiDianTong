package com.example.yidiantong.util;

public class Constant {
    //服务器地址(IP地址，请替换为你电脑中的IP)
    public static final String API = "http://www.cn901.net:8111";
    //登录接口
    public static final String LOGIN = "/AppServer/ajax/userManage_login.do";
    //获取最新信息
    public static final String NEW_ITEM = "/AppServer/ajax/studentApp_getStudentPlan.do";
    //获取作业题信息
    public static final String HOMEWORK_ITEM = "/AppServer/ajax/studentApp_getJobDetails.do";
    //上传图片
    public static final String UPLOAD_IMAGE = "/AppServer/ajax/studentApp_saveBase64Image.do";
    //获取答题情况
    public static final String ANSWER_ITEM = "/AppServer/ajax/studentApp_getStudentAnswerList.do";
    //提交试题答案
    public static final String SUBMIT_ANSWER = "/AppServer/ajax/studentApp_saveAnswer.do";
    //最终提交作业
    public static final String SUBMIT_ANSWER_FINAL = "/AppServer/ajax/studentApp_saveStudentHomeWork.do";
}
