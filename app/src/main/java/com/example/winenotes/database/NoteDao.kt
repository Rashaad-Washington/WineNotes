package com.example.winenotes.database

import androidx.room.Dao
import androidx.room.Query

@Dao
interface NoteDao {
    @Query("SELECT * FROM Note")
    fun getAllNotes() : List<Note>

}