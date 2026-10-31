package com.example.edutrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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
        val spinnerSection = findViewById<Spinner>(R.id.spinnerSection)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)

        // Populate sections list
        val sectionsList = mutableListOf("ACT1A", "ACT1B", "ACT2A", "ACT2B", "General Section")

        // Fetch custom subjects created by teachers or sections created by admin
        val contentPrefs = getSharedPreferences("EduTrackContentPrefs", Context.MODE_PRIVATE)
        val customSubject = contentPrefs.getString("Subject / Course_title", null)
        if (!customSubject.isNullOrEmpty() && !sectionsList.contains(customSubject)) {
            sectionsList.add(customSubject)
        }

        if (spinnerSection != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sectionsList)
            spinnerSection.adapter = adapter
        }

        btnSignUp?.setOnClickListener {
            val fullName = etFullName?.text?.toString()?.trim() ?: ""
            val email = etEmail?.text?.toString()?.trim() ?: ""
            val password = etPassword?.text?.toString()?.trim() ?: ""
            val confirmPassword = etConfirmPassword?.text?.toString()?.trim() ?: ""
            val selectedSection = spinnerSection?.selectedItem?.toString() ?: "General Section"

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                etConfirmPassword?.error = "Passwords do not match"
                return@setOnClickListener
            }

            // Save user credentials using email as a unique key suffix,
            // and also keep track of the current active profile keys for simple login lookup.
            val sharedPref = getSharedPreferences("EduTrackUserPrefs", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()

            // Save general current profile
            editor.putString("USER_FULL_NAME", fullName)
            editor.putString("USER_NAME", fullName)
            editor.putString("USER_EMAIL", email)
            editor.putString("USER_PASSWORD", password)
            editor.putString("USER_SECTION", selectedSection)
            editor.putString("USER_ROLE", "student")
            editor.putBoolean("IS_LOGGED_IN", true)

            // Save indexed credentials so multiple accounts can persist
            editor.putString("pass_$email", password)
            editor.putString("name_$email", fullName)
            editor.putString("section_$email", selectedSection)
            editor.apply()

            Toast.makeText(this, "Account Created Successfully for $selectedSection!", Toast.LENGTH_SHORT).show()

            // Navigate dynamically to Dashboard
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        // Navigate back to Login Activity
        tvLoginLink?.setOnClickListener {
            finish()
        }
    }
}
