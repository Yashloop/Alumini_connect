package com.example.vecalumini.ui.event

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vecalumini.databinding.ActivityEventBinding
import com.example.vecalumini.ui.post.Post
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class EventCalendarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventBinding
    private val db = FirebaseFirestore.getInstance()
    private val allEvents = mutableListOf<Post>()
    private val selectedDateEvents = mutableListOf<Post>()
    private lateinit var adapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = EventAdapter(selectedDateEvents)
        binding.recyclerViewEvents.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewEvents.adapter = adapter

        // Fetch all events
        db.collection("posts")
            .whereEqualTo("type", "Event")
            .get()
            .addOnSuccessListener { result ->
                allEvents.clear()
                for (doc in result) {
                    val post = doc.toObject(Post::class.java)
                    allEvents.add(post)
                }
                // Filter today's events on load
                filterEvents(binding.calendarView.date)
            }

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedMillis = getStartOfDayMillis(year, month, dayOfMonth)
            filterEvents(selectedMillis)
        }
    }

    private fun getStartOfDayMillis(year: Int, month: Int, day: Int): Long {
        val cal = Calendar.getInstance()
        cal.set(year, month, day, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun filterEvents(selectedMillis: Long) {
        selectedDateEvents.clear()
        val start = selectedMillis
        val end = selectedMillis + 24 * 60 * 60 * 1000

        val filtered = allEvents.filter {
            it.eventDate != null && it.eventDate in start until end
        }

        selectedDateEvents.addAll(filtered)
        adapter.notifyDataSetChanged()
    }
}
