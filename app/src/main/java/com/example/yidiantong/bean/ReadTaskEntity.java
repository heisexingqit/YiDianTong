package com.example.yidiantong.bean;

import java.io.Serializable;
import java.util.List;

public class ReadTaskEntity implements Serializable {
    public String recordId;
    public String imageId;
    public String imageUrl;
    public int showNum;
    public int initNum;
    public List<ZYRecordAnswerEntity> ZYRecordAnswerList;
    public String imageOrder;
    public boolean isNew;
}
