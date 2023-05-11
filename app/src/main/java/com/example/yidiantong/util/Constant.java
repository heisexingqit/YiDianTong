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
    //获取“我的”版本更新信息
    public static final String CHECK_VERSION = "/AppServer/ajax/teacherApp_checkAppVersion.do";
    //获取错题本学科信息
    public static final String ERROR_QUE_SUBJECT = "/AppServer/ajax/studentApp_ErrorQueGetSubject.do";
    //修改错题已读状态
    public static final String ERROR_QUE_UPDATE_STATUS = "/AppServer/ajax/studentApp_ErrorQueUpdateStatus.do";
    //获取错题信息
    public static final String ERROR_QUE_GET_QUESTION = "/AppServer/ajax/studentApp_ErrorQueGetQuestion.do";
    //获取错题详情信息
    public static final String ERROR_QUE_ANSWER_QUESTION = "/AppServer/ajax/studentApp_ErrorQueAnswerQuestion.do";
    // 标记错题，将其移入错题回收站
    public static final String ERROR_QUE_BIAOJI = "/AppServer/ajax/studentApp_ErrorQueBiaoji.do";
    // 获取错题回收站
    public static final String ERROR_QUE_GET_QUESTION_RECYCLE = "/AppServer/ajax/studentApp_ErrorQueGetQuestionRecycle.do";
    // 恢复错题
    public static final String ERROR_QUE_REMOVE= "/AppServer/ajax/studentApp_ErrorQueRemove.do";
}
