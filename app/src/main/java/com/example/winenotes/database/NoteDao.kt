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

    @Query("SELECT * FROM note")
    fun getAllNotes(): List<Note>

    @Query("SELECT * FROM note ORDER BY title")
    fun getAllNotesTitle(): List<Note>

    @Query("SELECT * FROM note ORDER BY lastModified")
    fun getAllNotesLastModified(): List<Note>

    @Query("SELECT * FROM note WHERE id = :noteId")
    fun getNote(noteId : Long) : Note

    @Update
    fun updateNote(note : Note)
}