package com.example.winenotes.database

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Note(
    @PrimaryKey val id: Long,
    @ColumnInfo val title : String,
    @ColumnInfo val notes : String,
    @ColumnInfo val lastModified : String
) {
    fun toLog() {
        Log.i("ID", "${id}")
    }

}