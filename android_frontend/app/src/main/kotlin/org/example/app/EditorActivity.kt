package org.example.app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText

/**
 * EditorActivity allows creating a new note or editing an existing one.
 */
// PUBLIC_INTERFACE
class EditorActivity : AppCompatActivity() {

    private lateinit var repository: NotesRepository
    private var noteId: Long? = null

    private lateinit var titleEdit: TextInputEditText
    private lateinit var contentEdit: TextInputEditText

    /**
     * Android lifecycle entrypoint. Initializes repository, reads note ID, and sets up UI.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        repository = NotesRepository(this)

        titleEdit = findViewById(R.id.inputTitle)
        contentEdit = findViewById(R.id.inputContent)

        noteId = intent.getLongExtra(EXTRA_NOTE_ID, -1L).takeIf { it != -1L }

        if (noteId != null) {
            supportActionBar?.title = getString(R.string.title_edit_note)
            val note = repository.getNoteById(noteId!!)
            if (note != null) {
                titleEdit.setText(note.title)
                contentEdit.setText(note.content)
            }
        } else {
            supportActionBar?.title = getString(R.string.title_new_note)
        }
    }

    /**
     * Inflates the editor menu, which includes a Save action.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        return true
    }

    /**
     * Handles toolbar actions including save and Up navigation.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            R.id.action_save -> {
                saveNote()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveNote() {
        val title = titleEdit.text?.toString()?.trim().orEmpty()
        val content = contentEdit.text?.toString()?.trim().orEmpty()

        if (title.isEmpty()) {
            titleEdit.error = getString(R.string.error_title_required)
            return
        }

        if (noteId == null) {
            // Create
            repository.insertNote(title, content)
        } else {
            // Update
            repository.updateNote(noteId!!, title, content)
        }
        finish()
    }

    companion object {
        // PUBLIC_INTERFACE
        const val EXTRA_NOTE_ID: String = "extra_note_id"
    }
}
