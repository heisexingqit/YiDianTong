package com.example.yidiantong.bean;

public class HomeworkPreviewEntity {
    public String questionContent;
    public String questionName;
    public String answer;
    public boolean show;
    public String analysis;

    @Override
    public String toString() {
        return "HomeworkPreviewEntity{" +
                ", questionName='" + questionName + '\'' +
                ", answer='" + answer + '\'' +
                ", show=" + show +
                ", analysis='" + analysis + '\'' +
                '}';


    }
}
