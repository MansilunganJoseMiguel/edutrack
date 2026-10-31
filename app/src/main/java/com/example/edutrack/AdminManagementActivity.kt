package com.example.edutrack

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView

class AdminManagementActivity : AppCompatActivity() {

    private lateinit var etNewSectionName: EditText
    private lateinit var btnSaveSection: Button
    private lateinit var layoutAdminStudentSubmissions: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_management)

        val toolbar = findViewById<Toolbar>(R.id.toolbarAdminMgmt)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        etNewSectionName = findViewById(R.id.etNewSectionName)
        btnSaveSection = findViewById(R.id.btnSaveSection)
        layoutAdminStudentSubmissions = findViewById(R.id.layoutAdminStudentSubmissions)

        // 1. Admin creates a new section
        btnSaveSection.setOnClickListener {
            val sectionName = etNewSectionName.text.toString().trim()
            if (sectionName.isNotEmpty()) {
                val prefs = getSharedPreferences("EduTrackAdminPrefs", Context.MODE_PRIVATE)
                // Save custom section list or latest section
                prefs.edit().putString("CREATED_SECTION_$sectionName", sectionName).apply()

                Toast.makeText(this, "Section \"$sectionName\" created successfully!", Toast.LENGTH_SHORT).show()
                etNewSectionName.text.clear()
            } else {
                Toast.makeText(this, "Please enter a section name.", Toast.LENGTH_SHORT).show()
            }
        }

        loadStudentSubmissions()
    }

    private fun loadStudentSubmissions() {
        layoutAdminStudentSubmissions.removeAllViews()

        // Fetch student account details and submissions from local storage
        val userPrefs = getSharedPreferences("EduTrackUserPrefs", Context.MODE_PRIVATE)
        val studentName = userPrefs.getString("USER_NAME", "Sample Student (Andrea)")
        val studentEmail = userPrefs.getString("USER_EMAIL", "student@gmail.com")
        val studentSection = userPrefs.getString("USER_SECTION", "General Section")

        // Fetch submissions or content statuses
        val contentPrefs = getSharedPreferences("EduTrackContentPrefs", Context.MODE_PRIVATE)
        val quizTitle = contentPrefs.getString("Quiz_title", "No Quizzes Posted")
        val projectTitle = contentPrefs.getString("Project_title", "No Projects Posted")
        val lessonTitle = contentPrefs.getString("Lesson_title", "No Lessons Posted")

        // Build a monitoring card for the student
        val cardView = CardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
            radius = 8f
            cardElevation = 2f
        }

        val cardLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val tvStudentInfo = TextView(this).apply {
            text = "👤 $studentName ($studentEmail)\n🏫 Section: $studentSection"
            textSize = 15f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setTextColor(resources.getColor(android.R.color.black))
            setPadding(0, 0, 0, 8)
        }

        val tvSubmissionsHeader = TextView(this).apply {
            text = "📋 Submitted Activities & Work:"
            textSize = 13f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setTextColor(resources.getColor(android.R.color.holo_blue_dark))
            setPadding(0, 4, 0, 4)
        }

        val tvQuizStatus = TextView(this).apply {
            text = "• Quiz: $quizTitle (Turned In / Pending)"
            textSize = 13f
            setTextColor(resources.getColor(android.R.color.darker_gray))
        }

        val tvProjectStatus = TextView(this).apply {
            text = "• Project: $projectTitle (Turned In / Pending)"
            textSize = 13f
            setTextColor(resources.getColor(android.R.color.darker_gray))
        }

        val tvLessonStatus = TextView(this).apply {
            text = "• Lesson Activity: $lessonTitle (Viewed)"
            textSize = 13f
            setTextColor(resources.getColor(android.R.color.darker_gray))
        }

        cardLayout.addView(tvStudentInfo)
        cardLayout.addView(tvSubmissionsHeader)
        cardLayout.addView(tvQuizStatus)
        cardLayout.addView(tvProjectStatus)
        cardLayout.addView(tvLessonStatus)

        cardView.addView(cardLayout)
        layoutAdminStudentSubmissions.addView(cardView)
    }
}