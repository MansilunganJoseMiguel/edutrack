package com.example.edutrack

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

// Data model for categorized items
data class TrackItem(
    val title: String,
    val subject: String, // "IS-DSA 213", "MAD 213", "CT-WA 213", "CC-IM 213", "IS-DCN 213"
    val category: String, // "ACTIVITIES", "QUIZZES", or "PROJECTS"
    val status: String // "Submitted", "Pending", etc.
)

class DetailActivity : AppCompatActivity() {

    private lateinit var layoutProfileContainer: LinearLayout
    private lateinit var layoutActivitiesContainer: LinearLayout
    private lateinit var imgProfile: ImageView
    private lateinit var tvSelectedFile: TextView
    private lateinit var tvContentList: TextView
    private lateinit var etSearch: EditText

    private var currentSubjectFilter = "ALL"
    private var currentCategory = "ACTIVITIES"

    // Updated master item list with your new subject codes
    private val masterItemList = listOf(
        // Activities
        TrackItem("Binary Search Tree Assignment", "IS-DSA 213", "ACTIVITIES", "Submitted"),
        TrackItem("Graph Algorithms Problem Set", "IS-DSA 213", "ACTIVITIES", "Pending - Due Today"),
        TrackItem("Mobile Layout Design Exercise", "MAD 213", "ACTIVITIES", "Submitted"),
        TrackItem("Event Handling Lab Activity", "MAD 213", "ACTIVITIES", "Pending - Due Tomorrow"),
        TrackItem("Responsive Web Template", "CT-WA 213", "ACTIVITIES", "Submitted"),
        TrackItem("JavaScript Form Validation", "CT-WA 213", "ACTIVITIES", "Pending - Due Friday"),
        TrackItem("ERD Normalization Activity", "CC-IM 213", "ACTIVITIES", "Submitted"),
        TrackItem("SQL Joins & Subqueries Lab", "CC-IM 213", "ACTIVITIES", "Pending - Due Next Week"),
        TrackItem("IP Addressing & Subnetting Sheet", "IS-DCN 213", "ACTIVITIES", "Submitted"),
        TrackItem("Packet Tracer Configuration", "IS-DCN 213", "ACTIVITIES", "Pending - Due Thursday"),

        // Quizzes
        TrackItem("Sorting & Searching Quiz", "IS-DSA 213", "QUIZZES", "Completed - Score: 92%"),
        TrackItem("Android Lifecycle Pop Quiz", "MAD 213", "QUIZZES", "Completed - Score: 88%"),
        TrackItem("CSS Flexbox & Grid Quiz", "CT-WA 213", "QUIZZES", "Upcoming"),
        TrackItem("Relational Algebra Quiz", "CC-IM 213", "QUIZZES", "Completed - Score: 95%"),
        TrackItem("OSI & TCP/IP Model Quiz", "IS-DCN 213", "QUIZZES", "Upcoming"),

        // Projects
        TrackItem("Data Structures Benchmark Project", "IS-DSA 213", "PROJECTS", "In Progress"),
        TrackItem("EduTrack Mobile Application", "MAD 213", "PROJECTS", "In Progress"),
        TrackItem("Full-Stack Student Portal Website", "CT-WA 213", "PROJECTS", "In Progress"),
        TrackItem("Database System Implementation", "CC-IM 213", "PROJECTS", "Submitted"),
        TrackItem("Network Topology Simulation", "IS-DCN 213", "PROJECTS", "In Progress")
    )

    // File launcher for uploading submissions
    private val selectFileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val fileUri: Uri? = result.data?.data
            if (fileUri != null) {
                val fileName = fileUri.path?.substringAfterLast('/') ?: "File Attached"
                tvSelectedFile.text = "Attached: $fileName"
                Toast.makeText(this, "File attached successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Profile picture picker launcher
    private val selectProfilePicLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val imageUri: Uri? = result.data?.data
            if (imageUri != null) {
                imgProfile.setImageURI(imageUri)
                Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val btnBack = findViewById<Button>(R.id.btnBack)
        val tvSectionTitle = findViewById<TextView>(R.id.tvSectionTitle)
        val btnUploadFile = findViewById<Button>(R.id.btnUploadFile)
        val btnChangeProfilePic = findViewById<Button>(R.id.btnChangeProfilePic)

        // Subject Filter Buttons
        val btnFilterAll = findViewById<Button>(R.id.btnFilterAll)
        val btnFilterDsa = findViewById<Button>(R.id.btnFilterDsa)
        val btnFilterMad = findViewById<Button>(R.id.btnFilterMad)
        val btnFilterWa = findViewById<Button>(R.id.btnFilterWa)
        val btnFilterIm = findViewById<Button>(R.id.btnFilterIm)
        val btnFilterDcn = findViewById<Button>(R.id.btnFilterDcn)

        layoutProfileContainer = findViewById(R.id.layoutProfileContainer)
        layoutActivitiesContainer = findViewById(R.id.layoutActivitiesContainer)
        imgProfile = findViewById(R.id.imgProfile)
        tvSelectedFile = findViewById(R.id.tvSelectedFile)
        tvContentList = findViewById(R.id.tvContentList)
        etSearch = findViewById(R.id.etSearch)

        val sharedPref = getSharedPreferences("EduTrackUserPrefs", Context.MODE_PRIVATE)
        val dynamicName = sharedPref.getString("USER_FULL_NAME", "Jose Miguel T. Mansilungan")
        val dynamicEmail = sharedPref.getString("USER_EMAIL", "jose.mansilungan@edutrack.edu")

        val tvProfileName = findViewById<TextView>(R.id.tvProfileName)
        val tvProfileEmail = findViewById<TextView>(R.id.tvProfileEmail)
        val tvProfileDetails = findViewById<TextView>(R.id.tvProfileDetails)

        btnBack.setOnClickListener { finish() }

        // Set User Profile Details
        tvProfileName?.text = dynamicName
        tvProfileEmail?.text = dynamicEmail
        tvProfileDetails?.text = """
            Role: Student
            Course: BS Information Technology
            Enrolled Subjects: 5 (IS-DSA 213, MAD 213, CT-WA 213, CC-IM 213, IS-DCN 213)
            Completed Activities: 20
        """.trimIndent()

        // Determine what category was tapped (PROFILE, ACTIVITIES, QUIZZES, or PROJECTS)
        val sectionType = intent.getStringExtra("SECTION_TYPE") ?: "ACTIVITIES"
        currentCategory = sectionType.uppercase()

        if (currentCategory == "PROFILE") {
            tvSectionTitle.text = "👤 User Profile"
            layoutProfileContainer.visibility = View.VISIBLE
            layoutActivitiesContainer.visibility = View.GONE
        } else {
            tvSectionTitle.text = when(currentCategory) {
                "QUIZZES" -> "❓ Quizzes Tracker"
                "PROJECTS" -> "📁 Projects Tracker"
                else -> "📝 Activities Tracker"
            }
            layoutProfileContainer.visibility = View.GONE
            layoutActivitiesContainer.visibility = View.VISIBLE

            filterAndRenderItems()
        }

        // Subject Filter Button Listeners
        btnFilterAll?.setOnClickListener {
            currentSubjectFilter = "ALL"
            filterAndRenderItems()
        }
        btnFilterDsa?.setOnClickListener {
            currentSubjectFilter = "IS-DSA 213"
            filterAndRenderItems()
        }
        btnFilterMad?.setOnClickListener {
            currentSubjectFilter = "MAD 213"
            filterAndRenderItems()
        }
        btnFilterWa?.setOnClickListener {
            currentSubjectFilter = "CT-WA 213"
            filterAndRenderItems()
        }
        btnFilterIm?.setOnClickListener {
            currentSubjectFilter = "CC-IM 213"
            filterAndRenderItems()
        }
        btnFilterDcn?.setOnClickListener {
            currentSubjectFilter = "IS-DCN 213"
            filterAndRenderItems()
        }

        // Search text watcher
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterAndRenderItems()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // File pickers
        btnUploadFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            selectFileLauncher.launch(Intent.createChooser(intent, "Select File to Upload"))
        }

        btnChangeProfilePic.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            selectProfilePicLauncher.launch(Intent.createChooser(intent, "Select Profile Image"))
        }
    }

    private fun filterAndRenderItems() {
        val query = etSearch.text.toString().trim().lowercase()

        // Filter by Category -> Subject -> Search Query
        val filteredList = masterItemList.filter { item ->
            val matchCategory = (item.category == currentCategory || currentCategory == "SUBJECTS")
            val matchSubject = (currentSubjectFilter == "ALL" || item.subject.equals(currentSubjectFilter, ignoreCase = true))
            val matchSearch = item.title.lowercase().contains(query) || item.status.lowercase().contains(query)

            matchCategory && matchSubject && matchSearch
        }

        if (filteredList.isEmpty()) {
            tvContentList.text = "No items found for the selected subject."
        } else {
            val formattedItems = filteredList.map { item ->
                "📌 [${item.subject}] ${item.title}\n   Status: ${item.status}"
            }
            tvContentList.text = formattedItems.joinToString("\n\n")
        }
    }
}