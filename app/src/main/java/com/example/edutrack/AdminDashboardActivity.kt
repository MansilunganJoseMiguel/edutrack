package com.example.edutrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val btnLogoutAdmin = findViewById<Button>(R.id.btnLogoutAdmin)
        btnLogoutAdmin?.setOnClickListener {
            // Clear login session preferences
            val sharedPref = getSharedPreferences("EduTrackUserPrefs", Context.MODE_PRIVATE)
            sharedPref.edit().putBoolean("IS_LOGGED_IN", false).apply()

            // Redirect back to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        val btnManageUsers = findViewById<Button>(R.id.btnManageUsers)
        btnManageUsers?.setOnClickListener {
            val intent = Intent(this, AdminManagementActivity::class.java)
            startActivity(intent)
        }

        val btnManageCourses = findViewById<Button>(R.id.btnManageCourses)
        btnManageCourses?.setOnClickListener {
            val intent = Intent(this, AdminManagementActivity::class.java)
            startActivity(intent)
        }
    }
}
