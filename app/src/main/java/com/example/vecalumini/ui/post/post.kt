package com.example.vecalumini.ui.post

data class Post(
    val uid: String = "",
    val email: String = "",
    val type: String = "",
    val content: String = "",
    val name: String = "",
    val department: String = "",
    val timestamp: Long = 0L,
    val eventDate: Long? = null  // <-- add this
)
