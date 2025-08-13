package org.example.app

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns

/**
 * NotesRepository provides CRUD operations backed by SQLite for the Notes app.
 */
// PUBLIC_INTERFACE
class NotesRepository(context: Context) {

    private val dbHelper = NotesDbHelper(context.applicationContext)

    // PUBLIC_INTERFACE
    fun getAllNotes(): List<Note> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            NotesDbHelper.TABLE_NOTES,
            arrayOf(
                NotesDbHelper.COL_ID,
                NotesDbHelper.COL_TITLE,
                NotesDbHelper.COL_CONTENT,
                NotesDbHelper.COL_UPDATED_AT
            ),
            null, null, null, null,
            "${NotesDbHelper.COL_UPDATED_AT} DESC"
        )
        cursor.use {
            val notes = mutableListOf<Note>()
            while (it.moveToNext()) {
                notes.add(cursorToNote(it))
            }
            return notes
        }
    }

    // PUBLIC_INTERFACE
    fun getNoteById(id: Long): Note? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            NotesDbHelper.TABLE_NOTES,
            arrayOf(
                NotesDbHelper.COL_ID,
                NotesDbHelper.COL_TITLE,
                NotesDbHelper.COL_CONTENT,
                NotesDbHelper.COL_UPDATED_AT
            ),
            "${BaseColumns._ID}=?",
            arrayOf(id.toString()),
            null, null, null
        )
        cursor.use {
            return if (it.moveToFirst()) cursorToNote(it) else null
        }
    }

    // PUBLIC_INTERFACE
    fun insertNote(title: String, content: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(NotesDbHelper.COL_TITLE, title)
            put(NotesDbHelper.COL_CONTENT, content)
            put(NotesDbHelper.COL_UPDATED_AT, System.currentTimeMillis())
        }
        return db.insert(NotesDbHelper.TABLE_NOTES, null, values)
    }

    // PUBLIC_INTERFACE
    fun updateNote(id: Long, title: String, content: String): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(NotesDbHelper.COL_TITLE, title)
            put(NotesDbHelper.COL_CONTENT, content)
            put(NotesDbHelper.COL_UPDATED_AT, System.currentTimeMillis())
        }
        return db.update(
            NotesDbHelper.TABLE_NOTES,
            values,
            "${NotesDbHelper.COL_ID}=?",
            arrayOf(id.toString())
        )
    }

    // PUBLIC_INTERFACE
    fun deleteNote(id: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            NotesDbHelper.TABLE_NOTES,
            "${NotesDbHelper.COL_ID}=?",
            arrayOf(id.toString())
        )
    }

    private fun cursorToNote(cursor: Cursor): Note {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(NotesDbHelper.COL_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(NotesDbHelper.COL_TITLE))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(NotesDbHelper.COL_CONTENT))
        val updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow(NotesDbHelper.COL_UPDATED_AT))
        return Note(id, title, content, updatedAt)
    }
}
