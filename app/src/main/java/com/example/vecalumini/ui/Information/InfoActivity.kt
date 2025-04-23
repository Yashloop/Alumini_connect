package com.example.vecalumini.ui.Information

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vecalumini.databinding.ActivityPostListBinding
import com.example.vecalumini.ui.post.Post
import com.example.vecalumini.ui.post.PostAdapter
import com.google.firebase.firestore.FirebaseFirestore

class InfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostListBinding
    private lateinit var postAdapter: PostAdapter
    private val postList = mutableListOf<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        postAdapter = PostAdapter(postList)
        binding.recyclerView.adapter = postAdapter

        fetchPosts()
    }

    private fun fetchPosts() {
        Log.d("Info", "Fetching info posts from Firestore...")

        FirebaseFirestore.getInstance()
            .collection("posts")
            .whereEqualTo("type", "Information")
            .get()
            .addOnSuccessListener { result ->
                postList.clear()
                for (document in result) {
                    val post = document.toObject(Post::class.java)
                    postList.add(post)
                }
                postAdapter.notifyDataSetChanged()
                Log.d("Info", "Information posts fetched: ${postList.size}")
            }
            .addOnFailureListener { e ->
                Log.e("Info", "Error fetching info: ${e.message}", e)
            }
    }
}
