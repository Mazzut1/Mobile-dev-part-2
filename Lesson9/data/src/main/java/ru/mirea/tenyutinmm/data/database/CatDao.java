package ru.mirea.tenyutinmm.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface CatDao {
    @Query("SELECT * FROM cats")
    List<CatEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CatEntity> cats);
}