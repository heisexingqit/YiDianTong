package com.example.yidiantong.util;

import java.io.UnsupportedEncodingException;

public interface TLearnPlanAddInterface {
    void submit(String startTime, String endTime, String ketang, String ketangId, String clas, String classId, String assignType, String stuIds, String stuNames, String learnType, String flag, int zouyeType, int zouyeFlag);
    void setLearnPlanId(String learnPlanId);
}
