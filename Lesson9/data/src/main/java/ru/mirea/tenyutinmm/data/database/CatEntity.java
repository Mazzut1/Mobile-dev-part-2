package ru.mirea.tenyutinmm.data.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cats")
public class CatEntity {
    @PrimaryKey
    @NonNull
    public String id;
    public String url;
}