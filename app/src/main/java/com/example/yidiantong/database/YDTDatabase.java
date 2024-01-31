package com.example.yidiantong.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.yidiantong.dao.HomeworkStuAnswerDao;
import com.example.yidiantong.entity.HomeworkStuAnswerInfo;

@Database(entities = {HomeworkStuAnswerInfo.class}, version=1, exportSchema = false)
public abstract class YDTDatabase extends RoomDatabase  {
    public abstract HomeworkStuAnswerDao homeworkStuAnswerDao();

}
