package org.example.app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * MainActivity shows a list of notes with search and a FAB to add a new note.
 */
// PUBLIC_INTERFACE
class MainActivity : AppCompatActivity(), NotesAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotesAdapter
    private lateinit var repository: NotesRepository

    /**
     * Android lifecycle entrypoint. Initializes UI, DB repository and sets up listeners.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        repository = NotesRepository(this)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = NotesAdapter(mutableListOf(), this)
        recyclerView.adapter = adapter

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, EditorActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Reload notes whenever the activity resumes to reflect changes from the editor.
     */
    override fun onResume() {
        super.onResume()
        loadNotes()
    }

    private fun loadNotes() {
        val notes = repository.getAllNotes()
        adapter.setNotes(notes)
    }

    /**
     * Inflates the search menu and configures SearchView for filtering notes.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchItem: MenuItem? = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.queryHint = getString(R.string.hint_search_notes)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText.orEmpty())
                return true
            }
        })

        return true
    }

    /**
     * Handles click on a list item to open it for editing.
     */
// PUBLIC_INTERFACE
    override fun onItemClick(note: Note) {
        val intent = Intent(this, EditorActivity::class.java)
        intent.putExtra(EditorActivity.EXTRA_NOTE_ID, note.id)
        startActivity(intent)
    }

    /**
     * Handles long-click to delete a note.
     */
// PUBLIC_INTERFACE
    override fun onItemLongClick(note: Note) {
        // Simple delete with immediate action for brevity; production apps should confirm with a dialog.
        repository.deleteNote(note.id!!)
        loadNotes()
    }
}
