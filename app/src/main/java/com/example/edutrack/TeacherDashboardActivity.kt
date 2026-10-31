package com.example.edutrack

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class TeacherDashboardActivity : AppCompatActivity() {

    private lateinit var layoutTeacherPosts: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_dashboard)

        layoutTeacherPosts = findViewById(R.id.layoutTeacherPosts)

        // FAB to open creation screen
        findViewById<View>(R.id.fabCreateAssignment).setOnClickListener {
            startActivity(Intent(this, CreateContentActivity::class.java))
        }

        // Handle Logout
        findViewById<View>(R.id.btnLogoutTeacher).setOnClickListener {
            val sharedPref = getSharedPreferences("EduTrackUserPrefs", Context.MODE_PRIVATE)
            sharedPref.edit().putBoolean("IS_LOGGED_IN", false).apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh the list of uploaded posts whenever returning to dashboard
        loadUploadedPosts()
    }

    private fun loadUploadedPosts() {
        layoutTeacherPosts.removeAllViews()

        val prefs = getSharedPreferences("EduTrackContentPrefs", Context.MODE_PRIVATE)
        val contentTypes = listOf("Subject / Course", "Lesson", "Quiz", "Project")

        var hasPosts = false

        for (type in contentTypes) {
            val title = prefs.getString("${type}_title", null)
            val desc = prefs.getString("${type}_desc", null)
            val due = prefs.getString("${type}_due", "No Due Date")

            if (!title.isNullOrEmpty()) {
                hasPosts = true
                addTeacherPostCard(type, title, desc ?: "", due ?: "No Due Date")
            }
        }

        if (!hasPosts) {
            val emptyView = TextView(this).apply {
                text = "You haven't uploaded any content yet.\nTap the '+' button below to add your first lesson or activity."
                textSize = 14f
                setTextColor(resources.getColor(android.R.color.darker_gray))
                setPadding(16, 24, 16, 16)
            }
            layoutTeacherPosts.addView(emptyView)
        }
    }

    private fun addTeacherPostCard(type: String, title: String, description: String, dueDate: String) {
        val cardView = CardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16)
            }
            radius = 12f
            cardElevation = 2f
        }

        val cardLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        // Post Type Header Badge
        val badgeView = TextView(this).apply {
            text = "📌 $type"
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(resources.getColor(android.R.color.holo_blue_dark))
        }

        // Title View
        val titleView = TextView(this).apply {
            text = title
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(resources.getColor(android.R.color.black))
            setPadding(0, 4, 0, 4)
        }

        // Description View
        val descView = TextView(this).apply {
            text = description
            textSize = 14f
            setTextColor(resources.getColor(android.R.color.darker_gray))
        }

        // Due Date View
        val dueView = TextView(this).apply {
            text = "📅 Due Date: $dueDate"
            textSize = 12f
            setTextColor(resources.getColor(android.R.color.holo_red_dark))
            setPadding(0, 6, 0, 12)
        }

        // Action Buttons Layout (Update / Delete)
        val actionLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val btnUpdate = Button(this).apply {
            text = "Update"
            textSize = 12f
            setBackgroundColor(resources.getColor(android.R.color.holo_blue_light))
            setTextColor(resources.getColor(android.R.color.white))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(0, 0, 8, 0)
            }
            setOnClickListener {
                showUpdateDialog(type, title, description, dueDate)
            }
        }

        val btnDelete = Button(this).apply {
            text = "Delete"
            textSize = 12f
            setBackgroundColor(resources.getColor(android.R.color.holo_red_light))
            setTextColor(resources.getColor(android.R.color.white))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            setOnClickListener {
                showDeleteConfirmation(type, title)
            }
        }

        actionLayout.addView(btnUpdate)
        actionLayout.addView(btnDelete)

        cardLayout.addView(badgeView)
        cardLayout.addView(titleView)
        if (description.isNotEmpty()) cardLayout.addView(descView)
        cardLayout.addView(dueView)
        cardLayout.addView(actionLayout)

        cardView.addView(cardLayout)
        layoutTeacherPosts.addView(cardView)
    }

    // --- UPDATE DIALOG ---
    private fun showUpdateDialog(type: String, currentTitle: String, currentDesc: String, currentDue: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Update $type")

        val dialogLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 16, 32, 16)
        }

        val etTitle = EditText(this).apply {
            hint = "Title"
            setText(currentTitle)
        }

        val etDesc = EditText(this).apply {
            hint = "Description / Instructions"
            setText(currentDesc)
        }

        dialogLayout.addView(etTitle)
        dialogLayout.addView(etDesc)
        builder.setView(dialogLayout)

        builder.setPositiveButton("Save Changes") { _, _ ->
            val updatedTitle = etTitle.text.toString().trim()
            val updatedDesc = etDesc.text.toString().trim()

            if (updatedTitle.isNotEmpty()) {
                val prefs = getSharedPreferences("EduTrackContentPrefs", Context.MODE_PRIVATE)
                prefs.edit().apply {
                    putString("${type}_title", updatedTitle)
                    putString("${type}_desc", updatedDesc)
                    apply()
                }
                Toast.makeText(this, "$type updated!", Toast.LENGTH_SHORT).show()
                loadUploadedPosts() // Refresh dashboard
            } else {
                Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    // --- DELETE CONFIRMATION ---
    private fun showDeleteConfirmation(type: String, title: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete $type")
            .setMessage("Are you sure you want to delete \"$title\"? It will also be removed from student dashboards.")
            .setPositiveButton("Delete") { _, _ ->
                val prefs = getSharedPreferences("EduTrackContentPrefs", Context.MODE_PRIVATE)
                prefs.edit().apply {
                    remove("${type}_title")
                    remove("${type}_desc")
                    remove("${type}_due")
                    apply()
                }
                Toast.makeText(this, "$type deleted successfully!", Toast.LENGTH_SHORT).show()
                loadUploadedPosts() // Refresh dashboard
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
