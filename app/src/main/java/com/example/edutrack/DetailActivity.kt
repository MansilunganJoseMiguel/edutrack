package com.example.edutrack

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class DetailActivity : AppCompatActivity() {

    private lateinit var layoutAttachedFiles: LinearLayout
    private lateinit var btnAddFile: Button
    private lateinit var btnSubmitWork: Button
    private lateinit var textSubmissionStatus: TextView

    private val PICK_FILE_REQUEST_CODE = 101
    private var attachedFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val toolbar = findViewById<Toolbar>(R.id.toolbarStudentDetail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val textDetailType = findViewById<TextView>(R.id.textDetailType)
        val textDetailTitle = findViewById<TextView>(R.id.textDetailTitle)
        val textDetailDue = findViewById<TextView>(R.id.textDetailDue)
        val textDetailDescription = findViewById<TextView>(R.id.textDetailDescription)

        layoutAttachedFiles = findViewById(R.id.layoutAttachedFiles)
        btnAddFile = findViewById(R.id.btnAddFile)
        btnSubmitWork = findViewById(R.id.btnSubmitWork)
        textSubmissionStatus = findViewById(R.id.textSubmissionStatus)

        // Get which type of item was clicked from the Dashboard intent
        val contentType = intent.getStringExtra("CONTENT_TYPE") ?: "Lesson"

        // Fetch saved teacher content from SharedPreferences
        val prefs = getSharedPreferences("EduTrackContentPrefs", Context.MODE_PRIVATE)
        val title = prefs.getString("${contentType}_title", "Untitled Content")
        val desc = prefs.getString("${contentType}_desc", "No instructions provided.")
        val due = prefs.getString("${contentType}_due", "No Due Date")

        // Populate UI with teacher's uploaded data
        textDetailType.text = "📌 $contentType"
        textDetailTitle.text = title
        textDetailDue.text = "📅 Due: $due"
        textDetailDescription.text = if (desc.isNullOrEmpty()) "No instructions provided." else desc

        // Handle clicking "+ Add or create file" to open phone file manager
        btnAddFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*" // Allows choosing PDFs, images, docs, etc.
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/pdf", "image/*", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
            }
            startActivityForResult(Intent.createChooser(intent, "Select File to Attach"), PICK_FILE_REQUEST_CODE)
        }

        // Handle clicking "Turn In"
        btnSubmitWork.setOnClickListener {
            if (attachedFileUri != null) {
                textSubmissionStatus.text = "Turned In"
                textSubmissionStatus.setTextColor(resources.getColor(android.R.color.holo_green_dark))
                btnSubmitWork.isEnabled = false
                btnSubmitWork.text = "Submitted"
                btnAddFile.isEnabled = false
                Toast.makeText(this, "Assignment submitted successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please attach a file before turning in.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Capture the selected file from phone storage
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                attachedFileUri = uri
                layoutAttachedFiles.visibility = View.VISIBLE
                layoutAttachedFiles.removeAllViews()

                // Display the selected file name dynamically
                val fileTextView = TextView(this).apply {
                    text = "📄 ${uri.lastPathSegment ?: "Attached_File.pdf"}"
                    textSize = 14f
                    setTextColor(resources.getColor(android.R.color.darker_gray))
                    setPadding(8, 8, 8, 8)
                }
                layoutAttachedFiles.addView(fileTextView)

                Toast.makeText(this, "File attached successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}