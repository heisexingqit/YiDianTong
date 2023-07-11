package com.example.yidiantong.bean;

import java.util.List;

public class LiveAddParamsEntity {
    List<LiveSubjectEntity> subjectList;
    List<KeyValueEntity> mList;

    public List<LiveSubjectEntity> getSubjectList() {
        return subjectList;
    }

    public void setSubjectList(List<LiveSubjectEntity> subjectList) {
        this.subjectList = subjectList;
    }

    public List<KeyValueEntity> getmList() {
        return mList;
    }

    public void setmList(List<KeyValueEntity> mList) {
        this.mList = mList;
    }

    @Override
    public String toString() {
        return "LiveAddParamsEntity{" +
                "subjectList=" + subjectList +
                ", mList=" + mList +
                '}';
    }

    public class LiveSubjectEntity {
        private List<KeyValueEntity> ketangList;
        private String subjectId;
        private String subjectName;

        public List<KeyValueEntity> getKetangList() {
            return ketangList;
        }

        public void setKetangList(List<KeyValueEntity> ketangList) {
            this.ketangList = ketangList;
        }

        public String getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(String subjectId) {
            this.subjectId = subjectId;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        @Override
        public String toString() {
            return "LiveSubject{" +
                    "ketangList=" + ketangList +
                    ", subjectId='" + subjectId + '\'' +
                    ", subjectName='" + subjectName + '\'' +
                    '}';
        }


    }
}
