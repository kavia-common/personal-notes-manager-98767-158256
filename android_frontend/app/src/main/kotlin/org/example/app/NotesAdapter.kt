package org.example.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import java.util.Date
import java.util.Locale

/**
 * NotesAdapter binds note items to a RecyclerView and supports simple filtering by text.
 */
// PUBLIC_INTERFACE
class NotesAdapter(
    private val notes: MutableList<Note>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    private val allNotes: MutableList<Note> = mutableListOf()

    init {
        allNotes.addAll(notes)
    }

    // PUBLIC_INTERFACE
    fun setNotes(newNotes: List<Note>) {
        notes.clear()
        notes.addAll(newNotes)
        allNotes.clear()
        allNotes.addAll(newNotes)
        notifyDataSetChanged()
    }

    // PUBLIC_INTERFACE
    fun filter(query: String) {
        val q = query.lowercase(Locale.getDefault())
        notes.clear()
        if (q.isBlank()) {
            notes.addAll(allNotes)
        } else {
            notes.addAll(allNotes.filter { n ->
                n.title.lowercase(Locale.getDefault()).contains(q) ||
                    n.content.lowercase(Locale.getDefault()).contains(q)
            })
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note, listener)
    }

    override fun getItemCount(): Int = notes.size

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card: CardView = itemView.findViewById(R.id.card)
        private val title: TextView = itemView.findViewById(R.id.title)
        private val content: TextView = itemView.findViewById(R.id.content)
        private val date: TextView = itemView.findViewById(R.id.date)

        fun bind(note: Note, listener: OnItemClickListener) {
            title.text = note.title
            content.text = note.content
            date.text = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                .format(Date(note.updatedAt))

            itemView.setOnClickListener { listener.onItemClick(note) }
            itemView.setOnLongClickListener {
                listener.onItemLongClick(note)
                true
            }
        }
    }

    /**
     * Listener interface for note interactions in the list.
     */
    // PUBLIC_INTERFACE
    interface OnItemClickListener {
        /** Called when a note is tapped. */
        // PUBLIC_INTERFACE
        fun onItemClick(note: Note)

        /** Called when a note is long-pressed (used here for delete). */
        // PUBLIC_INTERFACE
        fun onItemLongClick(note: Note)
    }
}
