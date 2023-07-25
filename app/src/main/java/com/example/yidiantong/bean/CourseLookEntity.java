package com.example.yidiantong.bean;

import java.util.List;

public class CourseLookEntity {
    public String type;
    public String userType;
    public String userNum;
    public String source;
    public String target;
    public String messageType;
    public String action;
    public String resId;
    public String resPath;
    public String learnPlanId;
    public String resRootPath;
    public String bagBean;
    public String desc;

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

    @Override
    public String toString() {
        return "CourseLookEntity{" +
                "type='" + type + '\'' +
                ", userType='" + userType + '\'' +
                ", userNum='" + userNum + '\'' +
                ", source='" + source + '\'' +
                ", target='" + target + '\'' +
                ", messageType='" + messageType + '\'' +
                ", action='" + action + '\'' +
                ", resId='" + resId + '\'' +
                ", resPath='" + resPath + '\'' +
                ", learnPlanId='" + learnPlanId + '\'' +
                ", resRootPath='" + resRootPath + '\'' +
                ", bagBean='" + bagBean + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }

    public List<CourseLookEntity.queList> period;

    public class queList{
        public String name;
        public String id;
        public String resourceId;
        public String questionId;
        public String type;
        public String questionType;
        public String questionTypeName;
        public String questionScore;
        public String questionValueList;
        public String questionAnswerText;
        public String links;
        public String imgSource;
        public String bagBean;
        public String desc;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getResourceId() {
            return resourceId;
        }

        public void setResourceId(String resourceId) {
            this.resourceId = resourceId;
        }

        public String getQuestionId() {
            return questionId;
        }

        public void setQuestionId(String questionId) {
            this.questionId = questionId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getQuestionType() {
            return questionType;
        }

        public void setQuestionType(String questionType) {
            this.questionType = questionType;
        }

        public String getQuestionTypeName() {
            return questionTypeName;
        }

        public void setQuestionTypeName(String questionTypeName) {
            this.questionTypeName = questionTypeName;
        }

        public String getQuestionScore() {
            return questionScore;
        }

        public void setQuestionScore(String questionScore) {
            this.questionScore = questionScore;
        }

        public String getQuestionValueList() {
            return questionValueList;
        }

        public void setQuestionValueList(String questionValueList) {
            this.questionValueList = questionValueList;
        }

        public String getQuestionAnswerText() {
            return questionAnswerText;
        }

        public void setQuestionAnswerText(String questionAnswerText) {
            this.questionAnswerText = questionAnswerText;
        }

        public String getLinks() {
            return links;
        }

        public void setLinks(String links) {
            this.links = links;
        }

        public String getImgSource() {
            return imgSource;
        }

        public void setImgSource(String imgSource) {
            this.imgSource = imgSource;
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

        @Override
        public String toString() {
            return "queList{" +
                    "name='" + name + '\'' +
                    ", id='" + id + '\'' +
                    ", resourceId='" + resourceId + '\'' +
                    ", questionId='" + questionId + '\'' +
                    ", type='" + type + '\'' +
                    ", questionType='" + questionType + '\'' +
                    ", questionTypeName='" + questionTypeName + '\'' +
                    ", questionScore='" + questionScore + '\'' +
                    ", questionValueList='" + questionValueList + '\'' +
                    ", questionAnswerText='" + questionAnswerText + '\'' +
                    ", links='" + links + '\'' +
                    ", imgSource='" + imgSource + '\'' +
                    ", bagBean='" + bagBean + '\'' +
                    ", desc='" + desc + '\'' +
                    '}';
        }
    }
}
