package com.example.edutrack

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateContentActivity : AppCompatActivity() {

    private lateinit var spinnerContentType: Spinner
    private lateinit var etContentTitle: EditText
    private lateinit var etContentDescription: EditText
    private lateinit var btnSelectDueDate: Button
    private lateinit var btnPublishContent: Button

    private var selectedDueDateStr: String = "No Due Date"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_content)

        val toolbar = findViewById<Toolbar>(R.id.toolbarCreate)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        spinnerContentType = findViewById(R.id.spinnerContentType)
        etContentTitle = findViewById(R.id.etContentTitle)
        etContentDescription = findViewById(R.id.etContentDescription)
        btnSelectDueDate = findViewById(R.id.btnSelectDueDate)
        btnPublishContent = findViewById(R.id.btnPublishContent)

        // Populate Spinner options matching Google Classroom/EduTrack categories
        val contentTypes = arrayOf("Subject / Course", "Lesson", "Quiz", "Project")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, contentTypes)
        spinnerContentType.adapter = adapter

        // Date Picker for Due Dates
        btnSelectDueDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                selectedDueDateStr = "${selectedMonth + 1}/$selectedDay/$selectedYear"
                btnSelectDueDate.text = "📅 Due: $selectedDueDateStr"
            }, year, month, day).show()
        }

        // Publish Action
        btnPublishContent.setOnClickListener {
            val type = spinnerContentType.selectedItem.toString()
            val title = etContentTitle.text.toString().trim()
            val description = etContentDescription.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a title or name.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save to SharedPreferences
            val prefs = getSharedPreferences("EduTrackContentPrefs", Context.MODE_PRIVATE)
            prefs.edit().apply {
                putString("${type}_title", title)
                putString("${type}_desc", description)
                putString("${type}_due", selectedDueDateStr)
                apply()
            }

            // --- SCHEDULE NOTIFICATION ---
            if (selectedDueDateStr != "No Due Date") {
                try {
                    val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                    val dueDateObj = sdf.parse(selectedDueDateStr)

                    if (dueDateObj != null) {
                        // Set notification trigger time (e.g., morning of the due date at 8:00 AM)
                        val calendar = Calendar.getInstance().apply {
                            time = dueDateObj
                            set(Calendar.HOUR_OF_DAY, 8)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                        }

                        if (calendar.timeInMillis > System.currentTimeMillis()) {
                            val intent = Intent(this, DueNotificationReceiver::class.java).apply {
                                putExtra("TITLE", title)
                                putExtra("TYPE", type)
                            }

                            val pendingIntent = PendingIntent.getBroadcast(
                                this,
                                title.hashCode(),
                                intent,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )

                            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                            alarmManager.setExact(
                                AlarmManager.RTC_WAKEUP,
                                calendar.timeInMillis,
                                pendingIntent
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            Toast.makeText(this, "$type published and reminder scheduled!", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
