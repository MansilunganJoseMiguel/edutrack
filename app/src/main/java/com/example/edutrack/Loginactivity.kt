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
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Retrieve dynamically stored user profile
            val sharedPref = getSharedPreferences("EduTrackUserPrefs", Context.MODE_PRIVATE)
            val savedEmail = sharedPref.getString("USER_EMAIL", "")
            val savedPassword = sharedPref.getString("USER_PASSWORD", "")

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (email == savedEmail && password == savedPassword) {
                // Save active login state
                sharedPref.edit().putBoolean("IS_LOGGED_IN", true).apply()

                Toast.makeText(this, "Welcome Back!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid email or password!", Toast.LENGTH_LONG).show()
                etPassword.error = "Incorrect credentials"
            }
        }

        tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}
