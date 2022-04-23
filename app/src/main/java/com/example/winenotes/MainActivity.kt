package com.example.winenotes

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.text.NumberFormat
import com.example.winenotes.database.Note
import com.example.winenotes.databinding.ActivityMainBinding


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
    }
    inner class MyViewHolder(val view: TextView) :
        RecyclerView.ViewHolder(view),
        View.OnClickListener {

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            Log.i("STATUS", "Clicked item ${adapterPosition}")

            val note = notes[adapterPosition]

            val message =
                "<b>Code:</b> <span style='color:blue;'>${note.id}</span><br>\n" +
                        "<b>Name:</b> <span style='color:blue;'>${note.title}</span><br>\n" +
                        "<b>Continent:</b> <span style='color:blue;'>${note.notes}</span><br>\n" +
                        "<b>Region:</b> <span style='color:blue;'>${note.lastModified}</span><br>\n" +
                        "<b>Population:</b> <span style='color:blue;'>" +
                        "</span>"
            Log.i("STATUS_HTML", message)

            val builder = AlertDialog.Builder(view!!.context)
                .setTitle("Country Info")
                .setPositiveButton("Close", null)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setMessage(
                    Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY)
                )
            } else {
                builder.setMessage(
                    Html.fromHtml(message)
                )
            }

            builder.show()
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
            if (notes.size > 0) {
                holder.view.setText(notes[position].title)
            }
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
        if (item.itemId == R.id.AddNote) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(
                "Add Note here"
            )
            builder.setPositiveButton("Okay", null)
            builder.show()
        }
        return super.onOptionsItemSelected(item)
    }
}
