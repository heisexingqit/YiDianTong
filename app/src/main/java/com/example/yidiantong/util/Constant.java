package com.example.yidiantong.util;

public class Constant {
    // 服务器地址
    public static String API = "http://120.133.15.246:8111";
    //public static String API = "http://www.cn901.net:8111";
    //public static final String API = "http://www.cn901.com:8181";
    // 直播课服务器地址
    public static final String API_LIVE = "http://www.cn901.com";

    // 教师或学生进行批改或查看时，判断作业或导学案状态
    public static final String CHECK_IN = "/AppServer/ajax/studentApp_checkTaskStatus.do";
    // 人促清除操作
    public static final String CHECK_OUT = "/AppServer/ajax/studentApp_deleteAccessControl.do";
    public static final String T_CHECK_OUT = "/AppServer/ajax/teacherApp_deleteAccessControl.do";

    public static final String CHANGE_PW = "/AppServer/ajax/userManage_updatePassword.do";
    // 登录接口
    public static final String LOGIN = "/AppServer/ajax/userManage_login.do";
    // 获取最新信息
    public static final String NEW_ITEM = "/AppServer/ajax/studentApp_getStudentPlan.do";
    // 获取作业题信息
    public static final String HOMEWORK_ITEM = "/AppServer/ajax/studentApp_getJobDetails.do";
    //获取“我的”版本更新信息
    public static final String CHECK_VERSION = "/AppServer/ajax/teacherApp_checkAppVersion.do";
    //记录忽略更新
    public static final String CHECK_VERSION_SAVE = "/AppServer/ajax/teacherApp_saveAppVersionUpdateRecord.do";
    //获取错题本学科信息
    public static final String ERROR_QUE_SUBJECT = "/AppServer/ajax/studentApp_ErrorQueGetSubject.do";
    //修改错题已读状态
    public static final String ERROR_QUE_UPDATE_STATUS = "/AppServer/ajax/studentApp_ErrorQueUpdateStatus.do";
    //获取错题信息
    public static final String ERROR_QUE_GET_QUESTION = "/AppServer/ajax/studentApp_ErrorQueGetQuestion.do";
    //获取错题详情信息
    public static final String ERROR_QUE_ANSWER_QUESTION = "/AppServer/ajax/studentApp_ErrorQueAnswerQuestion.do";
    // 错题本训练模式-提交答案判对错
    public static final String ERROR_QUE_ANSWER_SUBMIT = "/AppServer/ajax/studentApp_ErrorQueAnswer.do";
    public static final String ERROR_QUE_SELECTOR = "/AppServer/ajax/studentApp_ErrorQueGetSelectList.do";
    // 标记错题，将其移入错题回收站
    public static final String ERROR_QUE_BIAOJI = "/AppServer/ajax/studentApp_ErrorQueBiaoji.do";
    // 获取错题回收站
    public static final String ERROR_QUE_GET_QUESTION_RECYCLE = "/AppServer/ajax/studentApp_ErrorQueGetQuestionRecycle.do";
    // 恢复错题
    public static final String ERROR_QUE_REMOVE = "/AppServer/ajax/studentApp_ErrorQueRemove.do";

    // 上传图片
    public static final String UPLOAD_IMAGE = "/AppServer/ajax/studentApp_saveBase64Image.do";
    // 上传头像
    public static final String UPLOAD_HEAD_PHOTO = "/AppServer/ajax/studentApp_uploadUserPhoto.do";
    // 获取答题情况
    public static final String ANSWER_ITEM = "/AppServer/ajax/studentApp_getStudentAnswerList.do";
    // 提交试题答案
    public static final String SUBMIT_ANSWER = "/AppServer/ajax/studentApp_saveAnswer.do";
    // 最终提交作业
    public static final String SUBMIT_ANSWER_FINAL = "/AppServer/ajax/studentApp_saveStudentHomeWork.do";
    // 查看已批改作业
    public static final String AFTER_MARKED = "/AppServer/ajax/studentApp_getMarkedJob.do";

    // 公布答案
    public static final String PUBLISH_ANSWER = "/AppServer/ajax/teacherApp_publishZYPaperAnwer.do";
    public static final String CANCEL_ANSWER = "/AppServer/ajax/teacherApp_closeZYPaperAnwer.do";
    //获取答案是否公布状态
    public static final String GET_ANSWER_STATUS = "/AppServer/ajax/teacherApp_getZYPaperAnwerStatus.do";
    // 退出批改操作
    public static final String T_AFTER_MARKED = "/AppServer/ajax/teacherApp_deleteAccessControl.do";

    // 导学案查看
    public static final String LEARNPLAN_ITEM = "/AppServer/ajax/studentApp_getCatalog.do";

    // 导学案学生答题详情
    public static final String LEARNPLAN_ANSWER_ITEM = "/AppServer/ajax/studentApp_getstuAnswerLearnPlanList.do";

    // 导学案试题提交
    public static final String LEARNPLAN_SUBMIT_QUS_ITEM = "/AppServer/ajax/studentApp_stuSaveLpAnswer.do";

    // 导学案整体保存
    public static final String LEARNPLAN_SUBMIT_FIN_ITEM = "/AppServer/ajax/studentApp_savestuAnswerFromLearnPlan.do";

    // 导学案学习时间保存
    public static final String LEARNPLAN_SUBMIT_TIME = "/AppServer/ajax/studentApp_ saveLearnResTime.do?";

    // 学生端资源夹未查看提示
    public static final String GET_RESOURCE_IS_READ = "/AppServer/ajax/studentApp_checkMineFloder.do";

    // 学生端资源夹列表获取
    public static final String GET_RESOURCE_FOLDER_ITEM = "/AppServer/ajax/studentApp_getMineFloder.do";

    // 学生端资源夹资源获取
    public static final String GET_RESOURCE_FOLDER_RESOURCE = "/AppServer/ajax/studentApp_lookMineFloderFile.do";

    // 直播课列表
    public static final String LIVE_ITEM = "/AppServer/ajax/studentApp_getZBLiveList.do";

    // 教师端获取直播列表
    public static final String T_LIVE_ITEM = "/AppServer/ajax/teacherApp_getZBLiveList.do";

    // 教师端直播课获取学科，课堂，协作组
    public static final String T_LIVE_ADD_PARAMS = "/ShopGoods/ajax/livePlay_getMyKtListRN.do";

    // 教师端创建直播课
    public static final String T_LIVE_ADD = "/ShopGoods/ajax/livePlay_saveZbLive.do";

    // 教师端删除直播课
    public static final String T_LIVE_DELETE = "/ShopGoods/ajax/livePlay_deleteZbLive.do";

    // 教师端删除直播课
    public static final String T_LIVE_EDIT = "/ShopGoods/ajax/livePlay_editZbLive.do";

    // 设置公告或通知为已读状态
    public static final String READ_NOTICE = "/AppServer/ajax/studentApp_readNotice.do";
    // 学生选科列表查看
    public static final String GET_SELECT_COURSE_TASK_LIST = "/AppServer/ajax/studentApp_getSelectCourseTaskList.do";
    // 学生选科详情
    public static final String GET_SELECT_COURSE_TASK_DETIAL = "/AppServer/ajax/studentApp_getSelectCourseTaskDetial.do";
    // 学生选科结果保存
    public static final String SAVE_SELECT_SUBJECT = "/AppServer/ajax/studentApp_saveSelectsubject.do";
    // 学生端登录课堂一点通
    public static final String KETANGPLAYBYSTU = "/KeTangServer/ajax/ketang_clientKeTangPlayByStu.do";
    // 学生端请求消息队列
    public static final String GET_MESSAGE_LIST_BY_STU = "/KeTangServer/ajax/ketang_getMessageListByStu.do";
    //学生端生成ip对应二维码
    public static final String GET_QRCODE_URL = "/KeTangServer/ajax/ketang_ getQRcodeUrl.do";
    //学生端抢答发送给课堂一点通
    public static final String RESPONDER_FROM_APP = "/KeTangServer/ajax/ketang_responderFromApp.do";
    //学生端提交答案到服务器（客）
    public static final String SAVE_STU_ANSWER_FROM_APP = "/KeTangServer/ajax/ketang_saveStuAnswerFromApp.do";
    //学生端提交答案到服务器（主）
    public static final String SAVE_STU_SUB_ANSWER_FROM_APP = "/KeTangServer/ajax/ketang_saveStuSubjectiveAnswerFromApp.do";
    // 学生端保存图片到服务器
    public static final String SAVE_BASE64_IMAGE = "/KeTangServer/ajax/ketang_saveBase64Image.do";
    // 教师端最新信息
    public static final String T_NEW_ITEM = "/AppServer/ajax/teacherApp_getAllNews.do";

    // 教师授课资料信息
    public static final String T_TEACH_ITEM = "/AppServer/ajax/teacherApp_getLearnPlanOrPaperList.do";

    // 教师端统计第一模块信息
    public static final String T_REPORT_TOP = "/AppServer/ajax/teacherApp_anayGetSunNum.do";

    // 教师端统计第一模块学期信息
    public static final String T_REPORT_SEMESTER = "/AppServer/ajax/teacherApp_anayGetSchoolYearTerm.do";

    // 教师端统计第二模块课堂信息
    public static final String T_REPORT_CLASS = "/AppServer/ajax/teacherApp_anayGetKTNum.do";

    // 教师端统计第三模块作业信息
    public static final String T_REPORT_HW = "/AppServer/ajax/teacherApp_anayGetBZZYNum.do";

    // 教师端统计第四模块试题信息
    public static final String T_REPORT_QU = "/AppServer/ajax/teacherApp_anayGetPGQueNum.do";


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
    // 学生端获取知识点
    public static final String HOMEWORK_ADD_ZHISHIDIAN = "/AppServer/ajax/studentApp_getImgKnowledgeTree.do";
    // 学生端获取举一反三试题
    public static final String GET_JUYIFANSAN = "/AppServer/ajax/studentApp_getQuestionsJYFS.do";
    // 学生端获取巩固提升试题
    public static final String GET_GONGGUTISHENG= "/AppServer/ajax/studentApp_getQuestionsGGTS.do";
    // 学生端获取自主学习试题
    public static final String GET_ZIZHUXUEXI= "/AppServer/ajax/studentApp_getQuestionsZZXX.do";

    // 教师端获取题库
    public static final String T_HOMEWORK_GET_ALL_QUESTIONS = "/AppServer/ajax/teacherApp_getAllQuestions.do";

    // 教师端获取【试题】类型
    public static final String T_GET_QUESTION_TYPE = "/AppServer/ajax/teacherApp_getQuestionTypeList1.do";

    // 教师端获取【试卷】类型
    public static final String T_GET_PAPER_TYPE = "/AppServer/ajax/teacherApp_getPaperTypeList.do";

    // 教师端获取【资源】类型
    public static final String T_GET_RESOURCE_TYPE = "/AppServer/ajax/teacherApp_getResourceTypeList.do";

    // 教师端删除试卷
    public static final String T_DELETE_PAPER = "/AppServer/ajax/teacherApp_deletePaper.do";

    // 教师端删除导学案,微课,授课包
    public static final String T_DELETE_LEARN_PLAN = "/AppServer/ajax/teacherApp_deleteLearnPlan.do";

    // 教师端获取导学案pickList
    public static final String T_GET_LEARN_PLAN_PICKLIST = "//AppServer/ajax/teacherApp_getLpEditContent.do";

    // 教师端获取作业pickList
    public static final String T_GET_PAPER_PICKLIST = "/AppServer/ajax/teacherApp_getPaperEditContent.do";

    // 教师端-拍照发布作业-获取题型
    public static final String T_HOMEWORK_CAMERA_GET_TYPE = "/AppServer/ajax/teacherApp_phoneGetQueType.do";

    // 教师端-拍照发布作业-新建试题Id
    public static final String T_HOMEWORK_CAMERA_GET_ID = "/AppServer/ajax/teacherApp_phoneAddQuestion.do";

    // 教师端-拍照发布作业-保存试题信息
    public static final String T_HOMEWORK_CAMERA_SAVE = "/AppServer/ajax/teacherApp_phoneSaveQuestionAndPaper.do";

    // 教师端-拍照发布作业-布置作业
    public static final String T_HOMEWORK_CAMERA_ASSIGN = "/AppServer/ajax/teacherApp_phoneBuzhiZY.do";

    // 教师端-拍照发布作业-获取作业
    public static final String T_HOMEWORK_CAMARA_GET = "/AppServer/ajax/teacherApp_phoneEditZY.do";

    // 教师端-拍照布置作业-图片上传
    public static final String T_UPLOAD_IMAGE = "/AppServer/ajax/teacherApp_saveBase64Image.do";

    // 教师端发布通知获取班级列表
    public static final String T_BELL_ADD_CLASS = "/AppServer/ajax/teacherApp_publishNotice.do";

    // 教师端查看通知或公告接口
    //public static final String T_BELL_LOOK_NOTICE = "/AppServer/ajax/teacherApp_lookNotice.do";
    public static final String T_BELL_LOOK_NOTICE = "/AppServer/ajax/teacherApp_lookNoticeNew.do";

    // 教师端撤回通知或公告
    public static final String T_DELETE_NOTICE = "/AppServer/ajax/teacherApp_deleteNotice.do";

    //教师删除公告
    public static final String T_DELETE_ANN = "/AppServer/ajax/teacherApp_deleteManageNotice.do";

    // 教师端保存公告接口
    //public static final String T_BELL_SAVE_MANAGE_ANN = "/AppServer/ajax/teacherApp_saveManageNotice.do";
    public static final String T_BELL_SAVE_MANAGE_ANN = "/AppServer/ajax/teacherApp_saveManageNoticeNew.do";

    // 教师端保存通知接口
    public static final String T_BELL_SAVE_MANAGE_NOTICE = "/AppServer/ajax/teacherApp_saveNotice.do";

    // 教师端获得课堂列表
    public static final String T_HOMEWORK_GET_KETANG = "/AppServer/ajax/teacherApp_getKeTangList.do";

    // 教师端获得班级，小组，个人
    public static final String T_HOMEWORK_GET_KETANG_ITEM = "/AppServer/ajax/teacherApp_getClassStudentList.do";

    // 教师端获取空试卷ID
    public static final String T_HOMEWORK_GET_PAPER_ID = "/AppServer/ajax/teacherApp_createEmptyPaper.do";

    // 教师端布置作业+保存作业
    public static final String T_HOMEWORK_ASSIGN_SAVE = "/AppServer/ajax/teacherApp_assignJobToStudents.do";

    // 教师端获取导学案资源
    public static final String T_LEARN_PLAN_GET_ALL_RESOURCE = "/AppServer/ajax/teacherApp_selectResToCreateLp.do";

    // 教师端保存+布置导学案
    public static final String T_LEARN_PLAN_ASSIGN_SAVE = "/AppServer/ajax/teacherApp_releaseLearnPlan.do";

    // 教师端教师页面属性获取
    public static final String T_MAIN_TEACH_PARAM_GET = "/AppServer/ajax/teacherApp_getLpProperty.do";

    // 教师端教师页面属性设置
    public static final String T_MAIN_TEACH_PARAM_SAVE = "/AppServer/ajax/teacherApp_saveLpProperty.do";

    // 教师端修改通知或公告，返回对应的内容
    //public static final String T_BELL_GET_NOTICE_INFO = "/AppServer/ajax/teacherApp_getNoticeInfo.do";
    public static final String T_BELL_GET_NOTICE_INFO = "/AppServer/ajax/teacherApp_getNoticeInfoNew.do";

    // 教师端获取授课一点通是否上课
    public static final String GET_SKYDT_STATUS = "/AppServer/ajax/teacherApp_getSkydtStatus.do";

    // 教师端获取课堂
    public static final String T_CLIENT_KETANG_PLAY_BY_TEA = "/KeTangServer/ajax/ketang_clientKeTangPlayByTea.do";

    // 教师端连接授课一点通
    public static final String T_GET_MESSAGE_LIST_BY_TEA = "/KeTangServer/ajax/ketang_getMessageListByTea.do";

    // 教师端向授课一点通发消息
    public static final String T_SEND_MESSAGE = "/KeTangServer/ajax/ketang_sendMessage.do";

    // 教师端向授课一点通上传图片
    public static final String T_YDT_UPLOAD_IMAGE = "/KeTangServer/ajax/ketang_saveBase64Image.do";

    // 遥控器获取资源列表
    public static final String T_REMOVE_GET_RESOUCE_LIST = "/KeTangServer/ajax/ketang_getPeriodList.do";

    // 遥控器设置答案
    public static final String T_REMOVE_SET_ANSWER = "/KeTangServer/ajax/hwp_savePPTAnswer.do";

    // 遥控器获取答案面板参数
    public static final String T_REMOVE_GET_ANSWER_PARAMS = "/KeTangServer/ajax/hwp_getQueInfo.do";

    // 提分训练
    public static final String T_GET_TIFEN_TRAIN = "/AppServer/ajax/studentApp_getRecommendQueByRn.do";

    //学霸答案
    public static final String XUEBA_ANSWER = "/AppServer/ajax/studentApp_getXuebaQueAnswer.do";

    // 判断是否学霸答案
    public static final String T_CHECK_XUEBA_ANSWER = "/AppServer/ajax/teacherApp_getXuebaQueAnswer.do";

    // 设置取消学霸答案
    public static final String T_SET_XUEBA_ANSWER = "/AppServer/ajax/teacherApp_saveOrdeleteXuebaAnswer.do";

    // 替换学霸答案
    public static final String T_REPLACE_XUEBA_ANSWER = "/AppServer/ajax/teacherApp_replaceXuebaAnswer.do";

    // 协作组相关
    public static final String T_HOMEWORK_ASSIGN_GET_XZ = "/AppServer/ajax/teacherApp_getXiezuozuList.do";

    public static final String T_HOMEWORK_ASSIGN_GET_XZ_CLASS = "/AppServer/ajax/teacherApp_getXiezuozuMemberList.do";

    // 作业预览
    public static final String T_HOMEWORK_PREVIEW = "/AppServer/ajax/teacherApp_lookZyDetial.do";

    //获取作业，导学案批改状态
    public static final String T_HOMEWORK_GET_STATUS = "/AppServer/ajax/studentApp_getTaskStatus.do";


}
