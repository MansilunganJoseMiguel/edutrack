package com.example.edutrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)

        btnSignUp.setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                etConfirmPassword.error = "Passwords do not match"
                return@setOnClickListener
            }

            // Save user info dynamically to SharedPreferences
            val sharedPref = getSharedPreferences("EduTrackUserPrefs", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("USER_FULL_NAME", fullName)
            editor.putString("USER_EMAIL", email)
            editor.putString("USER_PASSWORD", password)
            editor.putBoolean("IS_LOGGED_IN", true)
            editor.apply()

            Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show()

            // Navigate dynamically to Dashboard
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        // Navigate back to Login Activity
        tvLoginLink.setOnClickListener {
            finish()
        }
    }
}
