package com.example.vecalumini.ui.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vecalumini.User
import com.example.vecalumini.databinding.ActivityUserListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserListActivity : AppCompatActivity(), UserAdapter.OnUserClickListener {

    private lateinit var binding: ActivityUserListBinding
    private lateinit var userAdapter: UserAdapter
    private val userList = mutableListOf<User>()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        userAdapter = UserAdapter(userList, this)
        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewUsers.adapter = userAdapter

        fetchUsersFromFirestore()
    }

    private fun fetchUsersFromFirestore() {
        FirebaseFirestore.getInstance().collection("users")
            .get()
            .addOnSuccessListener { result ->
                userList.clear()
                for (document in result) {
                    val user = document.toObject(User::class.java)

                    // Exclude current user and ensure name is present
                    if (user.uid != currentUserId && user.name.isNotBlank()) {
                        userList.add(user)
                    }
                }
                userAdapter.notifyDataSetChanged()

                // Show or hide views based on result
                if (userList.isEmpty()) {
                    binding.recyclerViewUsers.visibility = View.GONE
                    binding.textNoUsers.visibility = View.VISIBLE
                } else {
                    binding.recyclerViewUsers.visibility = View.VISIBLE
                    binding.textNoUsers.visibility = View.GONE
                }
            }
            .addOnFailureListener { e ->
                Log.e("UserListActivity", "Error fetching users", e)
                binding.recyclerViewUsers.visibility = View.GONE
                binding.textNoUsers.visibility = View.VISIBLE
            }
    }

    override fun onUserClick(user: User) {
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra("receiverId", user.uid)
            putExtra("receiverName", user.name)
        }
        startActivity(intent)
    }
}
