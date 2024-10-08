package com.example.yidiantong.bean;

import java.io.Serializable;
import java.util.List;

public class ReadTaskResultEntity implements Serializable {
    public String imageId;
    public String imageUrl;
    public List<ZYRecordAnswerEntity> ZYRecordAnswerList;
    public String imageOrder;
}
