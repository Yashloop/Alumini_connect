package com.example.vecalumini.ui.event

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vecalumini.R
import com.example.vecalumini.ui.post.Post
import java.text.SimpleDateFormat
import java.util.*

class EventAdapter(private val events: List<Post>) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.textUserName)
        val dept: TextView = itemView.findViewById(R.id.textUserDepartment)
        val date: TextView = itemView.findViewById(R.id.textPostDate)
        val content: TextView = itemView.findViewById(R.id.textPostContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_job_alert, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.name.text = event.name
        holder.dept.text = event.department
        holder.content.text = event.content

        val postDateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val eventDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        val postDate = postDateFormat.format(Date(event.timestamp))
        val eventDate = if (event.eventDate != null) eventDateFormat.format(Date(event.eventDate)) else "N/A"

        holder.date.text = "Posted on: $postDate\nEvent Date: $eventDate"
    }

    override fun getItemCount(): Int = events.size
}
