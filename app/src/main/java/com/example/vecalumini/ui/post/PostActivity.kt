package com.example.vecalumini.ui.post

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.vecalumini.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class PostActivity : AppCompatActivity() {

    private lateinit var spinner: Spinner
    private lateinit var contentEditText: EditText
    private lateinit var postButton: Button

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var selectedEventDate: Long? = null  // Store selected event date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_activity)

        spinner = findViewById(R.id.postTypeSpinner)
        contentEditText = findViewById(R.id.postContentEditText)
        postButton = findViewById(R.id.postButton)

        val postTypes = listOf("Job Alert", "Internship", "Information", "Notes", "Success Story", "Event")

        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, postTypes).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                val selectedType = parent.getItemAtPosition(position).toString()
                if (selectedType == "Event") {
                    showDatePickerDialog()
                } else {
                    selectedEventDate = null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        postButton.setOnClickListener {
            val type = spinner.selectedItem.toString()
            val content = contentEditText.text.toString().trim()
            val user = auth.currentUser

            if (content.isEmpty()) {
                Toast.makeText(this, "Please enter some content", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (type == "Event" && selectedEventDate == null) {
                Toast.makeText(this, "Please select a date for the event", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (type == "Notes" || type == "Success Story") {
                val input = EditText(this)
                input.hint = "Admin Password"
                android.app.AlertDialog.Builder(this)
                    .setTitle("Enter Admin Password")
                    .setView(input)
                    .setPositiveButton("OK") { _, _ ->
                        val password = input.text.toString()
                        if (password == "1234") {
                            uploadPost(type, content, user?.uid ?: "", user?.email ?: "")
                        } else {
                            Toast.makeText(this, "Incorrect password!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            } else {
                uploadPost(type, content, user?.uid ?: "", user?.email ?: "")
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth, 0, 0, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                selectedEventDate = calendar.timeInMillis
                Toast.makeText(this, "Event Date Selected", Toast.LENGTH_SHORT).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun uploadPost(type: String, content: String, uid: String, email: String) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val name = doc.getString("name") ?: "Unknown"
                val department = doc.getString("department") ?: "Unknown"

                val post = hashMapOf(
                    "uid" to uid,
                    "email" to email,
                    "type" to type,
                    "content" to content,
                    "name" to name,
                    "department" to department,
                    "timestamp" to System.currentTimeMillis()
                )

                val eventDate = selectedEventDate
                if (type == "Event" && eventDate != null) {
                    post["eventDate"] = eventDate
                }

                firestore.collection("posts").add(post)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Posted successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to post: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch user info.", Toast.LENGTH_SHORT).show()
            }
    }
}
