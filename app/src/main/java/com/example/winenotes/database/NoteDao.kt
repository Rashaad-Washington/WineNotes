package com.example.winenotes.database

import androidx.room.*

@Dao
interface NoteDao {
    @Insert
    fun addNote(note: Note) : Long

    @Delete
    fun deleteNote(note: Note)

    @Query("DELETE FROM Note")
    fun deleteAllNotes()

    @Query("SELECT * FROM note ORDER BY title, lastModified")
    fun getAllNotes(): List<Note>

    @Query("SELECT * FROM note WHERE id = :personId")
    fun getNote(personId : Long) : Note

    @Update
    fun updateNote(note : Note)
}