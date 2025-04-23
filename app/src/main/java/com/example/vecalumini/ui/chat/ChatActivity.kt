package com.example.vecalumini.ui.chat

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vecalumini.databinding.ActivityChatBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private val messageList = mutableListOf<Message>()

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private var senderName: String? = null
    private var receiverName: String? = null
    private lateinit var chatId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        senderName = auth.currentUser?.displayName
        receiverName = intent.getStringExtra("receiverName")

        if (senderName.isNullOrBlank() || receiverName.isNullOrBlank()) {
            Toast.makeText(this, "User information missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Generate a consistent chat ID (e.g., A_B or B_A always becomes A_B)
        chatId = generateChatId(senderName!!, receiverName!!)
        supportActionBar?.title = receiverName

        setupChatRecyclerView()
        listenForMessages()

        binding.buttonSend.setOnClickListener {
            val messageText = binding.editTextMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                binding.editTextMessage.setText("")
            }
        }
    }

    private fun generateChatId(userA: String, userB: String): String {
        return listOf(userA, userB).sorted().joinToString("_")
    }

    private fun setupChatRecyclerView() {
        chatAdapter = ChatAdapter(messageList, senderName.orEmpty())
        binding.recyclerViewChat.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = chatAdapter
        }
    }

    private fun sendMessage(messageText: String) {
        val message = Message(
            senderId = senderName.orEmpty(),
            receiverId = receiverName.orEmpty(),
            message = messageText,
            timestamp = Timestamp.now()
        )

        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .add(message)
            .addOnSuccessListener {
                Log.d("ChatActivity", "Message sent")
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
                Log.e("ChatActivity", "Send error", it)
            }
    }

    private fun listenForMessages() {
        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Error loading messages", Toast.LENGTH_SHORT).show()
                    Log.e("ChatActivity", "Listen error", e)
                    return@addSnapshotListener
                }

                messageList.clear()
                snapshots?.forEach { doc ->
                    val message = doc.toObject(Message::class.java)
                    messageList.add(message)
                }

                chatAdapter.notifyDataSetChanged()
                binding.recyclerViewChat.scrollToPosition(messageList.size - 1)
            }
    }
}
