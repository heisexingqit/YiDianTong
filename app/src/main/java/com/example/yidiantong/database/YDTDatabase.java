package com.example.yidiantong.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.yidiantong.dao.HomeworkStuAnswerDao;
import com.example.yidiantong.entity.HomeworkStuAnswerInfo;

@Database(entities = {HomeworkStuAnswerInfo.class}, version = 2, exportSchema = false)
public abstract class YDTDatabase extends RoomDatabase {
    public abstract HomeworkStuAnswerDao homeworkStuAnswerDao();

    // 定义 Migration 对象
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // 删除旧表
            database.execSQL("DROP TABLE IF EXISTS HomeworkStuAnswerInfo");

            // 创建新表
            database.execSQL("CREATE TABLE IF NOT EXISTS HomeworkStuAnswerInfo (" +
                    "userId TEXT NOT NULL, " +
                    "homeworkId TEXT NOT NULL, " +
                    "questionId TEXT NOT NULL, " +
                    "stuAnswer TEXT, " +
                    "userName TEXT, " +
                    "\"order\" INTEGER, " +
                    "updateDate TEXT, " +
                    "answerTime TEXT, " +
                    "useTime TEXT, " +
                    "type TEXT, " +
                    "PRIMARY KEY(userId, homeworkId, questionId))");
        }
    };

}
