package com.example.yidiantong.bean;

public class SchoolYearListEntity {
    private Integer schoolYearTermId;
    private String schoolYearName;
    private String schoolTermName;
    private int dbid;

    public SchoolYearListEntity() {
    }

    public Integer getSchoolYearTermId() {
        return schoolYearTermId;
    }

    public void setSchoolYearTermId(Integer schoolYearTermId) {
        this.schoolYearTermId = schoolYearTermId;
    }

    public String getSchoolYearName() {
        return schoolYearName;
    }

    public void setSchoolYearName(String schoolYearName) {
        this.schoolYearName = schoolYearName;
    }

    public String getSchoolTermName() {
        return schoolTermName;
    }

    public void setSchoolTermName(String schoolTermName) {
        this.schoolTermName = schoolTermName;
    }

    public int getDbid() {
        return dbid;
    }

    public void setDbid(int dbid) {
        this.dbid = dbid;
    }

    @Override
    public String toString() {
        return "SchoolYearListEntity{" +
                "schoolYearTermId=" + schoolYearTermId +
                ", schoolYearName='" + schoolYearName + '\'' +
                ", schoolTermName='" + schoolTermName + '\'' +
                ", dbid=" + dbid +
                '}';
    }
}
