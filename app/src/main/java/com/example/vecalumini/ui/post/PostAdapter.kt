package com.example.vecalumini.ui.post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vecalumini.R
import java.text.SimpleDateFormat
import java.util.*

class PostAdapter(private val postList: List<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.textUserName)
        val deptText: TextView = itemView.findViewById(R.id.textUserDepartment)
        val dateText: TextView = itemView.findViewById(R.id.textPostDate)
        val contentText: TextView = itemView.findViewById(R.id.textPostContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_job_alert, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        holder.nameText.text = post.name
        holder.deptText.text = post.department
        holder.contentText.text = post.content

        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val postDate = sdf.format(Date(post.timestamp))

        if (post.type == "Event" && post.eventDate != null) {
            val eventDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val eventDateStr = eventDateFormat.format(Date(post.eventDate))
            holder.dateText.text = "Posted on: $postDate\nEvent Date: $eventDateStr"
        } else {
            holder.dateText.text = "Posted on: $postDate"
        }
    }

    override fun getItemCount(): Int = postList.size
}
