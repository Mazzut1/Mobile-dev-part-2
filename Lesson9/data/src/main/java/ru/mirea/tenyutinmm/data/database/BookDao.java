package ru.mirea.tenyutinmm.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface BookDao {
    @Insert
    void insert(BookEntity book);

    @Query("SELECT * FROM favorite_books ORDER BY id DESC")
    List<BookEntity> getAllBooks();
}