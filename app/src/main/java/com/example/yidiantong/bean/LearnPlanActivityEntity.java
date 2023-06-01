package com.example.yidiantong.bean;

import java.util.List;

public class LearnPlanActivityEntity {
    private String activityName;
    private List<LearnPlanItemEntity> resourceList;

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public List<LearnPlanItemEntity> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<LearnPlanItemEntity> resourceList) {
        this.resourceList = resourceList;
    }

    @Override
    public String toString() {
        return "LearnPlanActivityEntity{" +
                "activityName='" + activityName + '\'' +
                ", resourceList=" + resourceList +
                '}';
    }
}
