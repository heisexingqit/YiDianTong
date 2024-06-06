package com.example.yidiantong.bean;

import java.util.List;

public class HomeworkXieZuoEntity {
    public String teacherId;
    public String teacherName;
    public List<Ketang> ketangList;

    public class Ketang {
        public String value;
        public String key;

        @Override
        public String toString() {
            return "Ketang{" +
                    "value='" + value + '\'' +
                    ", key='" + key + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "HomeworkXiezuoEntity{" +
                "teacherId='" + teacherId + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", ketangList=" + ketangList +
                '}';
    }
}
