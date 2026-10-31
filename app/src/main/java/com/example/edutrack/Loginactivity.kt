package com.example.edutrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvCreateAccount = findViewById<TextView>(R.id.tvCreateAccount)

        btnLogin.setOnClickListener {
            val inputEmailOrUser = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (inputEmailOrUser.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPref = getSharedPreferences("EduTrackUserPrefs", Context.MODE_PRIVATE)

            // 1. Check for Admin Credentials (Hardcoded)
            if (inputEmailOrUser.equals("admin", ignoreCase = true) && password == "admin123") {
                sharedPref.edit().apply {
                    putBoolean("IS_LOGGED_IN", true)
                    putString("USER_ROLE", "admin")
                    apply()
                }
                Toast.makeText(this, "Welcome Admin!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, AdminDashboardActivity::class.java))
                finish()
                return@setOnClickListener
            }

            // 2. Check for Teacher Credentials (Must end with @deped.ph)
            if (inputEmailOrUser.endsWith("@deped.ph", ignoreCase = true)) {
                sharedPref.edit().apply {
                    putBoolean("IS_LOGGED_IN", true)
                    putString("USER_ROLE", "teacher")
                    putString("USER_EMAIL", inputEmailOrUser)
                    apply()
                }
                Toast.makeText(this, "Welcome Teacher!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, TeacherDashboardActivity::class.java))
                finish()
                return@setOnClickListener
            }

            // 3. Check for Student Credentials (Registered dynamic users)
            val savedEmail = sharedPref.getString("USER_EMAIL", "")
            val savedPassword = sharedPref.getString("USER_PASSWORD", "")

            val indexedPassword = sharedPref.getString("pass_$inputEmailOrUser", null)
            val indexedName = sharedPref.getString("name_$inputEmailOrUser", null)

            val isIndexedMatch = indexedPassword != null && indexedPassword == password
            val isGlobalMatch = (inputEmailOrUser.equals(savedEmail, ignoreCase = true) ||
                    inputEmailOrUser.equals(sharedPref.getString("USER_FULL_NAME", ""), ignoreCase = true))
                    && password == savedPassword

            if (isIndexedMatch || isGlobalMatch) {
                // Force active role to "student" upon successful student authentication
                sharedPref.edit().apply {
                    putBoolean("IS_LOGGED_IN", true)
                    putString("USER_ROLE", "student")

                    if (isIndexedMatch) {
                        putString("USER_EMAIL", inputEmailOrUser)
                        putString("USER_PASSWORD", password)
                        if (indexedName != null) {
                            putString("USER_FULL_NAME", indexedName)
                            putString("USER_NAME", indexedName)
                        }
                        putString("USER_SECTION", sharedPref.getString("section_$inputEmailOrUser", "General Section"))
                    }
                    apply()
                }

                Toast.makeText(this, "Welcome Student!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid email, username, or password!", Toast.LENGTH_LONG).show()
                etPassword.error = "Incorrect credentials"
            }
        }

        tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}