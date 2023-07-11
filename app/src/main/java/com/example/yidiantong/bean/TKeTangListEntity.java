package com.example.yidiantong.bean;

public class TKeTangListEntity {

    private String type;

    // teacher教师；student学生；ui教学app
    private String userType;

    // one 某个人；many很多人；ui教学app
    private String userNum;

    // 消息来源 教师和学生用户名，教学app：0
    private String source;

    // 消息接受对象 教师id
    private String target;

    // 0：控制信号；1：读信号；2：写信号；3：反馈信号
    private String messageType;

    // app端根据action进行页面切换
    private String action;

    // 消息对应处理类型，授课包内question试题页面；ppt；word；video；resourceWeb；
    // sign：签到页面；allAnalysus：学情页面；
    // questionAnswer开始答题，questionAnalysis开始单题分析
    private String actionType;

    // 资源id，获取当前显示的资源id
    private String resId;

    // 资源相对路径
    private String resPath;
    // 导学案Id
    private String learnPlanId;
    // 资源主机地址
    private String resRootPath;
    private String period;
    private String periodList;
    private String bagBean;
    private String desc;
    private String treeBean;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserNum() {
        return userNum;
    }

    public void setUserNum(String userNum) {
        this.userNum = userNum;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getResPath() {
        return resPath;
    }

    public void setResPath(String resPath) {
        this.resPath = resPath;
    }

    public String getLearnPlanId() {
        return learnPlanId;
    }

    public void setLearnPlanId(String learnPlanId) {
        this.learnPlanId = learnPlanId;
    }

    public String getResRootPath() {
        return resRootPath;
    }

    public void setResRootPath(String resRootPath) {
        this.resRootPath = resRootPath;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getPeriodList() {
        return periodList;
    }

    public void setPeriodList(String periodList) {
        this.periodList = periodList;
    }

    public String getBagBean() {
        return bagBean;
    }

    public void setBagBean(String bagBean) {
        this.bagBean = bagBean;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTreeBean() {
        return treeBean;
    }

    public void setTreeBean(String treeBean) {
        this.treeBean = treeBean;
    }

    @Override
    public String toString() {
        return "TKeTangListEntity{" +
                "type='" + type + '\'' +
                ", userType='" + userType + '\'' +
                ", userNum='" + userNum + '\'' +
                ", source='" + source + '\'' +
                ", target='" + target + '\'' +
                ", messageType='" + messageType + '\'' +
                ", action='" + action + '\'' +
                ", actionType='" + actionType + '\'' +
                ", resId='" + resId + '\'' +
                ", resPath='" + resPath + '\'' +
                ", learnPlanId='" + learnPlanId + '\'' +
                ", resRootPath='" + resRootPath + '\'' +
                ", period='" + period + '\'' +
                ", periodList='" + periodList + '\'' +
                ", bagBean='" + bagBean + '\'' +
                ", desc='" + desc + '\'' +
                ", treeBean='" + treeBean + '\'' +
                '}';
    }
}
