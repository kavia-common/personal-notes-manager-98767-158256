package org.example.app

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * NotesDbHelper manages creation and version management of the notes SQLite database.
 */
// PUBLIC_INTERFACE
class NotesDbHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_NOTES (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE TEXT NOT NULL,
                $COL_CONTENT TEXT NOT NULL,
                $COL_UPDATED_AT INTEGER NOT NULL
            );
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX idx_notes_updated_at ON $TABLE_NOTES($COL_UPDATED_AT)")
        db.execSQL("CREATE INDEX idx_notes_title ON $TABLE_NOTES($COL_TITLE)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "notes.db"
        const val DATABASE_VERSION = 1

        const val TABLE_NOTES = "notes"
        const val COL_ID = "_id"
        const val COL_TITLE = "title"
        const val COL_CONTENT = "content"
        const val COL_UPDATED_AT = "updated_at"
    }
}
