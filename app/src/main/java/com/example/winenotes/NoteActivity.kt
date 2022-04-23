package com.example.winenotes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.winenotes.database.AppDatabase
import com.example.winenotes.database.Note
import com.example.winenotes.database.NoteDao
import com.example.winenotes.databinding.NoteActivityBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class NoteActivity : AppCompatActivity() {
    private lateinit var binding: NoteActivityBinding
    private var purpose: String? = ""
    private var noteId : Long = -1




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NoteActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = getIntent()
        purpose = intent.getStringExtra(
            getString(R.string.intent_purpose_key)
        )

        if (purpose.equals(getString(R.string.intent_purpose_update_note))) {
            noteId = intent.getLongExtra(
                getString(R.string.intent_key_note_id),
                -1
            )
            // load exiting person from database
            CoroutineScope(Dispatchers.IO).launch {
                val note = AppDatabase.getDatabase(applicationContext)
                    .noteDao()
                    .getNote(noteId)

                withContext(Dispatchers.Main) {
                    binding.editTitleET.setText(note.title)
                    binding.editNoteET.setText(note.notes)
                }
            }
        }
        setTitle("${purpose} Name")
    }

    override fun onBackPressed() {
        val editTitle = binding.editTitleET.getText().toString().trim()
        if (editTitle.isEmpty()) {
            Toast.makeText(applicationContext, "Title can not be empty", Toast.LENGTH_LONG
            ).show()
            return
        }

        var editNotes = binding.editNoteET.getText().toString().trim()
        if (editNotes.isEmpty()) {
            editNotes = ""

        }

        CoroutineScope(Dispatchers.IO).launch {
            val noteDao = AppDatabase.getDatabase(applicationContext)
                .noteDao()

            var resultId : Long
            val now : Date = Date()
            val databaseDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            databaseDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
            var dateString : String = databaseDateFormat.format(now)
            if (purpose.equals(getString(R.string.intent_purpose_add_note)))  {
                val note = Note(0, editTitle, editNotes, dateString)
                noteId = noteDao.addNote(note)
                Log.i("Status_Name", "inserted new note ${note}")
            }   else {
                val note = Note(noteId, editTitle, editNotes, dateString)
                noteDao.updateNote(note)
                Log.i("STATUS_NAME", "updated existing note: ${note}")

            }
            Log.i("STATUS_NAME", "result_id: ${noteId}")

            val intent = Intent()

            intent.putExtra(
                getString(R.string.intent_key_note_id),
                noteId
            )

            withContext(Dispatchers.Main) {
                setResult(RESULT_OK, intent)
                super.onBackPressed()
            }
        }
        super.onBackPressed()
    }
}
