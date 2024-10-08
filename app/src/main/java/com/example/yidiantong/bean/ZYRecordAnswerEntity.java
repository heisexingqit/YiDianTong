package com.example.yidiantong.bean;

import java.io.Serializable;
import java.util.List;

public class ZYRecordAnswerEntity implements Serializable {
    public String path;
    public String score;
    public String id;
    public String text;
    public List<TextStatus> list;
    public String content;
    public String order;
    public String status;
    public String time;
    public class TextStatus{
        public String text;
        public String status;
    }
}
