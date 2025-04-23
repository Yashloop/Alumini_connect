package com.example.vecalumini

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vecalumini.databinding.ActivityUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveButton.setOnClickListener {
            showPasswordDialog()
        }
    }

    private fun showPasswordDialog() {
        val passwordInput = EditText(this)
        passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        passwordInput.hint = "Enter Password"

        AlertDialog.Builder(this)
            .setTitle("Confirm Password")
            .setMessage("To save your profile, please enter the password.")
            .setView(passwordInput)
            .setPositiveButton("Confirm") { dialog, _ ->
                val enteredPassword = passwordInput.text.toString().trim()
                if (enteredPassword == "vec_app") {
                    saveUserProfile()
                } else {
                    Toast.makeText(this, "Incorrect password. Profile not saved.", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun saveUserProfile() {
        val name = binding.nameInput.text.toString().trim()
        val gradYear = binding.gradYearInput.text.toString().trim()
        val degree = binding.degreeInput.text.toString().trim()
        val department = binding.departmentInput.text.toString().trim()
        val jobTitle = binding.jobTitleInput.text.toString().trim()
        val company = binding.companyInput.text.toString().trim()
        val location = binding.locationInput.text.toString().trim()
        val linkedin = binding.linkedinInput.text.toString().trim()
        val phone = binding.phoneInput.text.toString().trim()
        val bio = binding.bioInput.text.toString().trim()
        val email = auth.currentUser?.email ?: ""
        val phoneVisible = binding.phoneVisibilityCheckbox.isChecked
        val uid = auth.currentUser?.uid ?: return

        val userData = hashMapOf(
            "name" to name,
            "graduationYear" to gradYear,
            "degree" to degree,
            "department" to department,
            "jobTitle" to jobTitle,
            "company" to company,
            "location" to location,
            "linkedinUrl" to linkedin,
            "phone" to phone,
            "email" to email,
            "phoneVisible" to phoneVisible,
            "bio" to bio,
            "imageUrl" to null
        )

        firestore.collection("users").document(uid)
            .set(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error saving profile", Toast.LENGTH_SHORT).show()
            }
    }
}
