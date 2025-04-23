package com.example.vecalumini

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({
            val user = auth.currentUser
            if (user != null) {
                // Already signed in
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // First time or signed out
                startActivity(Intent(this, WelcomeActivity::class.java))
            }
            finish()
        }, 3000) // 3 seconds splash delay
    }
}
