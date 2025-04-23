package com.example.vecalumini

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val createAccountBtn: Button = findViewById(R.id.createAccountBtn)

        createAccountBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
