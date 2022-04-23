package com.example.winenotes
/*Rashaad Washington
Assignment 4
21 April 2022
 */
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.winenotes.database.AppDatabase
import kotlinx.coroutines.*
import com.example.winenotes.database.Note
import com.example.winenotes.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MyAdapter
    private val notes = mutableListOf<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerview.setLayoutManager(layoutManager)

        val dividerItemDecoration = DividerItemDecoration(
            applicationContext, layoutManager.getOrientation()
        )
        binding.recyclerview.addItemDecoration(dividerItemDecoration)

        adapter = MyAdapter()
        binding.recyclerview.setAdapter(adapter)

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val dao = db.noteDao()
            val results = dao.getAllNotes()
            Log.i("sadasd", "${results}")

            withContext(Dispatchers.Main) {
                notes.clear()
                notes.addAll(results)
                adapter.notifyDataSetChanged()
            }
        }
    }
    private val startForAddResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result : ActivityResult ->

            if (result.resultCode == Activity.RESULT_OK){
                loadAllNotes()
            }
        }
    private val startForUpdateResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result : ActivityResult ->

            if (result.resultCode == Activity.RESULT_OK) {
                // alternative - reload the whole database
                // good only for small databases
                loadAllNotes()
            }
        }

    private fun addNewNote(){
        val intent = Intent(applicationContext, NoteActivity::class.java)
        intent.putExtra(
            getString(R.string.intent_purpose_key),
            getString(R.string.intent_purpose_add_note)
        )
        startForAddResult.launch(intent)
    }




    inner class MyViewHolder(val view: TextView) :
        RecyclerView.ViewHolder(view),
        View.OnClickListener, View.OnLongClickListener {

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }
        override fun onClick(view: View?) {
            val intent = Intent(applicationContext, NoteActivity::class.java)

            intent.putExtra(
                getString(R.string.intent_purpose_key),
                getString(R.string.intent_purpose_update_note)
            )

            val note = notes[adapterPosition]
            intent.putExtra(
                getString(R.string.intent_key_note_id),
                note.id
            )

            startForUpdateResult.launch(intent)
            /*Log.i("STATUS", "Clicked item ${adapterPosition}")

            val note = notes[adapterPosition]
            val builder = AlertDialog.Builder(view!!.context)
                .setTitle(note.title)
                .setMessage(note.notes)
                .setPositiveButton("Close", null)
            builder.show()*/
        }

        override fun onLongClick(view: View?): Boolean {

            val note = notes[adapterPosition]

            val builder = AlertDialog.Builder(view!!.context)
                .setTitle("Confirm delete")
                .setMessage("Are you sure you want to delete " +
                        "${note.title}?")
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) {
                        dialogInterface, whichButton ->

                    CoroutineScope(Dispatchers.IO).launch {
                        AppDatabase.getDatabase(applicationContext)
                            .noteDao()
                            .deleteNote(note)

                        // alternative - reload the whole database
                        // good only for small databases
                        loadAllNotes()

                    }

                }
            builder.show()

            return true
        }


    }

    inner class MyAdapter :
        RecyclerView.Adapter<MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_view, parent, false) as TextView
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val note = notes[position]
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            parser.setTimeZone(TimeZone.getTimeZone("UTC"))
// convert the date string from the database to the Date object
            val dateInDatabase : Date = parser.parse(note.lastModified)
// create a formatter that will convert the date to the format
// you want the user to see on screen. This will use the
// time zone the user is currently in.
            val displayFormat = SimpleDateFormat("HH:mm a MM/yyyy ")
// convert the temporary Date object from the database
// to a string for the user to see
            val displayString : String = displayFormat.format(dateInDatabase)

            holder.view.setText(
                "${note.title}\nModified: ${displayString}"
            )

        }

        override fun getItemCount(): Int {
            return notes.size
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() == R.id.menu_item_add) {
            addNewNote()
            return true
        } else if (item.getItemId() == R.id.menu_clear_data) {
            deleteAllNotes()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteAllNotes() {
        val builder = AlertDialog.Builder(this)
            .setTitle("Confirm delete")
            .setMessage("Are you sure you want to delete all data?")
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) {
                    dialogInterface, whichButton ->

                CoroutineScope(Dispatchers.IO).launch {
                    AppDatabase.getDatabase(applicationContext)
                        .noteDao()
                        .deleteAllNotes()

                    // alternative - reload the whole database
                    // good only for small databases
                    loadAllNotes()

                }

            }
        builder.show()
    }

    private fun loadAllNotes() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val dao = db.noteDao()
            val results = dao.getAllNotes()
            for (person in results) {
                Log.i("STATUS_MAIN:", "read ${person}")
            }

            withContext(Dispatchers.Main) {
                notes.clear()
                notes.addAll(results)
                adapter.notifyDataSetChanged()
            }
        }
    }
}
