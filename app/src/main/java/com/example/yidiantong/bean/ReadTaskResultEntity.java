package com.example.yidiantong.bean;

import java.io.Serializable;
import java.util.List;

public class ReadTaskResultEntity implements Serializable {
    public String imageId;
    public String imageUrl;
    public List<ZYRecordAnswerEntity> ZYRecordAnswerList;
    public String imageOrder;
    public boolean isNew;
    @Override
    public String toString() {
        return "ReadTaskResultEntity{" +
                "imageId='" + imageId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", ZYRecordAnswerList=" + ZYRecordAnswerList +
                ", imageOrder='" + imageOrder + '\'' +
                ", isNew=" + isNew +
                '}';
    }

    public void getReciteId(){
        int id = 1;
        for (ZYRecordAnswerEntity entity: ZYRecordAnswerList) {
            if(entity.type.equals("2")){
                entity.reciteId = id+"";
                id++;
            }
        }
    }
}
