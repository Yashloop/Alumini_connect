package com.example.vecalumini.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.vecalumini.R
import com.example.vecalumini.databinding.FragmentHomeBinding
import com.example.vecalumini.ui.adapters.ImageSliderAdapter
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setupImageSlider()
        loadNotesFromFirestore()
        loadSuccessStoriesFromFirestore()
    }

    private fun setupImageSlider() {
        val imageList = listOf(
            R.drawable.new1,
            R.drawable.new2,
            R.drawable.new3
        )

        val adapter = ImageSliderAdapter(imageList)
        binding.newsSlider.adapter = adapter
        binding.newsSlider.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }

    private fun loadNotesFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("posts")
            .whereEqualTo("type", "Notes")
            .get() // removed .orderBy() to prevent FAILED_PRECONDITION
            .addOnSuccessListener { result ->
                binding.notesContainer.removeAllViews()
                if (result.isEmpty) {
                    addTextViewToContainer(binding.notesContainer, "No notes available.")
                } else {
                    for (doc in result) {
                        val noteText = doc.getString("content") ?: continue
                        addTextViewToContainer(binding.notesContainer, "\u2022 $noteText")
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load notes.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadSuccessStoriesFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("posts")
            .whereEqualTo("type", "Success Story")
            .get() // removed .orderBy() to avoid index issues
            .addOnSuccessListener { result ->
                binding.successStoriesContainer.removeAllViews()
                if (result.isEmpty) {
                    val noStory = layoutInflater.inflate(
                        R.layout.item_success_story_card,
                        binding.successStoriesContainer,
                        false
                    )
                    noStory.findViewById<TextView>(R.id.storyName).text = "No stories available"
                    noStory.findViewById<TextView>(R.id.storyDept).text = ""
                    noStory.findViewById<TextView>(R.id.storyContent).text = ""
                    binding.successStoriesContainer.addView(noStory)
                } else {
                    for (doc in result) {
                        val name = doc.getString("name") ?: ""
                        val department = doc.getString("department") ?: ""
                        val content = doc.getString("content") ?: ""

                        val storyView = layoutInflater.inflate(
                            R.layout.item_success_story_card,
                            binding.successStoriesContainer,
                            false
                        )
                        storyView.findViewById<TextView>(R.id.storyName).text = name
                        storyView.findViewById<TextView>(R.id.storyDept).text = department
                        storyView.findViewById<TextView>(R.id.storyContent).text = content
                        binding.successStoriesContainer.addView(storyView)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load stories.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addTextViewToContainer(container: ViewGroup, text: String) {
        val textView = TextView(requireContext()).apply {
            this.text = text
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            textSize = 16f
            setPadding(0, 4, 0, 4)
        }
        container.addView(textView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
