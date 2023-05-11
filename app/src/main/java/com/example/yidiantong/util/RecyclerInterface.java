package com.example.yidiantong.util;

public interface RecyclerInterface {
    void pageLast(String currentpage, String allpage);
    void pageNext(String currentpage, String allpage);
    void updatepage(String currentpage, String allpage);
}