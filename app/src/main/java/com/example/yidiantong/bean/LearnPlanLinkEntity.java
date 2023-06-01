package com.example.yidiantong.bean;

import java.util.List;

public class LearnPlanLinkEntity {
    private String link;
    private List<LearnPlanActivityEntity> activityList;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<LearnPlanActivityEntity> getActivityList() {
        return activityList;
    }

    public void setActivityList(List<LearnPlanActivityEntity> activityList) {
        this.activityList = activityList;
    }

    @Override
    public String toString() {
        return "LearnPlanLinkEntity{" +
                "link='" + link + '\'' +
                ", activityList=" + activityList +
                '}';
    }
}
