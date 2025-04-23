package com.example.vecalumini

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.vecalumini.ui.chat.UserListActivity

import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.vecalumini.databinding.ActivityMainBinding
import com.example.vecalumini.ui.Information.InfoActivity
import com.example.vecalumini.ui.Jobalert.JobAlertsActivity
import com.example.vecalumini.ui.event.EventCalendarActivity
import com.example.vecalumini.ui.internship.InternshipActivity
import com.example.vecalumini.ui.post.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, UserListActivity::class.java)
            startActivity(intent)
        }

        // Set up destinations for Navigation Component
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_gallery,
                R.id.nav_slideshow
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        // Handle Navigation Drawer item clicks
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_post -> startActivity(Intent(this, PostActivity::class.java))
                R.id.nav_job_alert -> startActivity(Intent(this, JobAlertsActivity::class.java))
                R.id.nav_internship -> startActivity(Intent(this, InternshipActivity::class.java))
                R.id.nav_information -> startActivity(Intent(this, InfoActivity::class.java))
                R.id.nav_chat -> startActivity(Intent(this, UserListActivity::class.java))
                R.id.nav_event -> {
                    startActivity(Intent(this, EventCalendarActivity::class.java))
                    true
                }

                else -> {
                    navController.navigate(menuItem.itemId) // For fragments
                }
            }
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }

        // Load user info into Navigation Drawer Header
        val headerView = navView.getHeaderView(0)
        val nameText = headerView.findViewById<TextView>(R.id.navUserName)
        val deptText = headerView.findViewById<TextView>(R.id.navUserDepartment)
        val yearText = headerView.findViewById<TextView>(R.id.navUserGraduation)

        FirebaseAuth.getInstance().currentUser?.let { user ->
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        nameText.text = doc.getString("name") ?: "Alumni"
                        deptText.text = doc.getString("department") ?: "Department"
                        yearText.text = doc.getString("graduationYear") ?: "Year"
                    }
                }
                .addOnFailureListener {
                    nameText.text = "Alumni"
                    deptText.text = "Department"
                    yearText.text = "Year"
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
