package com.example.yidiantong.util;

public class Constant {
    // 服务器地址
    public static final String API = "http://www.cn901.net:8111";
    // 登录接口
    public static final String LOGIN = "/AppServer/ajax/userManage_login.do";
    // 获取最新信息
    public static final String NEW_ITEM = "/AppServer/ajax/studentApp_getStudentPlan.do";
    // 获取作业题信息
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

    // 上传图片
    public static final String UPLOAD_IMAGE = "/AppServer/ajax/studentApp_saveBase64Image.do";
    // 获取答题情况
    public static final String ANSWER_ITEM = "/AppServer/ajax/studentApp_getStudentAnswerList.do";
    // 提交试题答案
    public static final String SUBMIT_ANSWER = "/AppServer/ajax/studentApp_saveAnswer.do";
    // 最终提交作业
    public static final String SUBMIT_ANSWER_FINAL = "/AppServer/ajax/studentApp_saveStudentHomeWork.do";
    // 查看已批改作业
    public static final String AFTER_MARKED = "/AppServer/ajax/studentApp_getMarkedJob.do";

    // 导学案查看
    public static final String LEARNPLAN_ITEM = "/AppServer/ajax/studentApp_getCatalog.do";

    // 教师端最新信息
    public static final String T_NEW_ITEM = "/AppServer/ajax/teacherApp_getAllNews.do";
    // 教师授课资料信息
    public static final String T_TEACH_ITEM = "/AppServer/ajax/teacherApp_getLearnPlanOrPaperList.do";

    // 教师端统计顶部信息
    public static final String T_REPORT_TOP = "/AppServer/ajax/teacherApp_anayGetSunNum.do";

    // 教师端获取作业和导学案学生list
    public static final String T_HOMEWORK_STUDENT_LIST = "/AppServer/ajax/teacherApp_getSubmitHomeworkStuList.do";

    // 教师端修改批改模式
    public static final String T_HOMEWORK_CHANGE_MOLD = "/AppServer/ajax/teacherApp_saveCoorectMode.do";

    // 教师端获取批改模式
    public static final String T_HOMEWORK_GET_MOLD = "/AppServer/ajax/teacherApp_getCoorectMode.do";

    // 教师端作业报告信息
    public static final String T_HOMEWORK_REPORT = "/AppServer/ajax/teacherApp_lookPresentation.do";

    // 教师端批改题目
    public static final String T_HOMEWORK_MARK = "/AppServer/ajax/teacherApp_correctingHomeWork.do";

    // 教师获取批改结果
    public static final String T_HOMEWORK_MARK_RESULT = "/AppServer/ajax/teacherApp_getCorrectResult.do";

    // 教师提交批改结果
    public static final String T_HOMEWORK_MARK_SUBMIT = "/AppServer/ajax/teacherApp_saveHomeWorkCorrectResult.do";

    // 教师端获取学段
    public static final String T_HOMEWORK_ADD_XUEDUAN = "/AppServer/ajax/teacherApp_getChannelList.do";

    // 教师端获取学科
    public static final String T_HOMEWORK_ADD_XUEKE = "/AppServer/ajax/teacherApp_getSubjectInfoList.do";

    // 教师端获取版本
    public static final String T_HOMEWORK_ADD_BANBEN = "/AppServer/ajax/teacherApp_getBookVersionList.do";

    // 教师端获取教材
    public static final String T_HOMEWORK_ADD_JIAOCAI = "/AppServer/ajax/teacherApp_getBookList.do";

    // 教师端获取知识点
    public static final String T_HOMEWORK_ADD_ZHISHIDIAN = "/AppServer/ajax/teacherApp_getKnowledgeTree.do";

    // 教师端获取题库
    public static final String T_HOMEWORK_ADD_ALLQUESTIONS = "/AppServer/ajax/teacherApp_getAllQuestions.do";

    // 教师端获取题库类型
    public static final String T_HOMEWORK_ADD_TYPE = "/AppServer/ajax/teacherApp_getQuestionTypeList1.do";

    // 教师端获得课堂列表
    public static final String T_HOMEWORK_GET_KETANG = "/AppServer/ajax/teacherApp_getKeTangList.do";

    // 教师端获得班级，小组，个人
    public static final String T_HOMEWORK_GET_KETANG_ITEM = "/AppServer/ajax/teacherApp_getClassStudentList.do";

    // 教师端获取空试卷ID
    public static final String T_HOMEWORK_GET_PAPER_ID = "/AppServer/ajax/teacherApp_createEmptyPaper.do";

    // 教师端布置作业+保存作业
    public static final String T_HOMEWORK_ASSIGN_SAVE = "/AppServer/ajax/teacherApp_assignJobToStudents.do";

}
