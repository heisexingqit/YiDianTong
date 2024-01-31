package com.example.yidiantong.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.yidiantong.entity.HomeworkStuAnswerInfo;

import java.util.List;

@Dao
public interface HomeworkStuAnswerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HomeworkStuAnswerInfo... info);

    @Query("SELECT * FROM HomeworkStuAnswerInfo WHERE userId = :userId AND homeworkId = :homeworkId ORDER BY `order` ASC")
    List<HomeworkStuAnswerInfo> queryByUserAndHomework(String userId, String homeworkId);

}
