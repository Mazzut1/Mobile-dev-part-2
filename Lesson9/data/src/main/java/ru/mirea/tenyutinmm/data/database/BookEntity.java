package ru.mirea.tenyutinmm.data.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_books")
public class BookEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String title;
}