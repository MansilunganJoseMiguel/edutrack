package com.example.edutrack

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible

class DashboardActivity : AppCompatActivity() {

    private var layoutStudentFeed: LinearLayout? = null
    private var textDashboardStudentName: TextView? = null
    private var textDashboardStudentSection: TextView? = null
    private var textWelcomeBanner: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. SharedPreferences Profile & Header Setup
        textDashboardStudentName = findViewById(R.id.textDashboardStudentName)
        textDashboardStudentSection = findViewById(R.id.textDashboardStudentSection)
        textWelcomeBanner = findViewById(R.id.textWelcomeBanner)

        val layoutDashboardProfile = findViewById<View>(R.id.layoutDashboardProfile)
        val profileDropdownMenu = findViewById<LinearLayout>(R.id.layoutProfileDropdown)
        val btnProfileDropdown = findViewById<TextView>(R.id.btnProfileDropdown)
        val btnSettingsDropdown = findViewById<TextView>(R.id.btnSettingsDropdown)
        val btnDropdownLogout = findViewById<TextView>(R.id.btnDropdownLogout)

        // 2. Navigation & Sidebar Element References
        val navDashboard = findViewById<TextView>(R.id.navDashboard)
        val layoutDashboardSubmenu = findViewById<LinearLayout>(R.id.layoutDashboardSubmenu)

        val navActivities = findViewById<TextView>(R.id.navActivities)
        val navQuizzes = findViewById<TextView>(R.id.navQuizzes)
        val navProjects = findViewById<TextView>(R.id.navProjects)
        val btnLogoutSidebar = findViewById<TextView>(R.id.btnLogout)

        layoutStudentFeed = findViewById(R.id.layoutStudentFeed)

        // Helper function to open DetailActivity with a specific section type
        fun openDetailScreen(sectionType: String) {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("SECTION_TYPE", sectionType)
            startActivity(intent)
        }

        // 3. DASHBOARD BUTTON TOGGLE: Slide options in and out with animation
        navDashboard?.setOnClickListener {
            if (layoutDashboardSubmenu != null) {
                if (layoutDashboardSubmenu.visibility == View.VISIBLE) {
                    collapse(layoutDashboardSubmenu)
                    navDashboard.text = "🏠  Dashboard ▾"
                } else {
                    expand(layoutDashboardSubmenu)
                    navDashboard.text = "🏠  Dashboard ▴"
                }
            }
        }

        // 4. Sidebar Section Click Listeners
        navActivities?.setOnClickListener { openDetailScreen("ACTIVITIES") }
        navQuizzes?.setOnClickListener { openDetailScreen("QUIZZES") }
        navProjects?.setOnClickListener { openDetailScreen("PROJECTS") }

        // 5. Top Profile Header Click Event (Toggle Dropdown Menu)
        layoutDashboardProfile?.setOnClickListener {
            if (profileDropdownMenu != null) {
                profileDropdownMenu.isVisible = !profileDropdownMenu.isVisible
            } else {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
        }

        // Dropdown Profile Items Click
        btnProfileDropdown?.setOnClickListener {
            profileDropdownMenu?.visibility = View.GONE
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        btnSettingsDropdown?.setOnClickListener {
            profileDropdownMenu?.visibility = View.GONE
            openDetailScreen("SETTINGS")
        }

        // 6. Logout Handling (Clears history and returns to LoginActivity)
        val performLogout = {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        btnLogoutSidebar?.setOnClickListener { performLogout() }
        btnDropdownLogout?.setOnClickListener { performLogout() }
    }

    override fun onResume() {
        super.onResume()
        // Refresh profile headers & feed every time student opens or returns to dashboard
        loadDashboardProfileInfo()
        loadTeacherPublishedContent()
    }

    private fun loadDashboardProfileInfo() {
        val sharedPref = getSharedPreferences("EduTrackUserPrefs", Context.MODE_PRIVATE)
        val fullName = sharedPref.getString("USER_FULL_NAME", null)
            ?: sharedPref.getString("USER_NAME", "Student") ?: "Student"
        val studentSection = sharedPref.getString("USER_SECTION", "General Section") ?: "General Section"

        // Update dashboard top profile bar
        textDashboardStudentName?.text = "$fullName ▾"
        textDashboardStudentSection?.text = "🏫 $studentSection"

        // Update welcome banner greeting
        val firstName = fullName.split(" ").firstOrNull() ?: fullName
        textWelcomeBanner?.text = "Welcome back,\n$firstName!"
    }

    private fun loadTeacherPublishedContent() {
        val feedLayout = layoutStudentFeed ?: return
        feedLayout.removeAllViews()

        val prefs = getSharedPreferences("EduTrackContentPrefs", Context.MODE_PRIVATE)

        // Categories published by teachers
        val contentTypes = listOf("Subject / Course", "Lesson", "Quiz", "Project")

        var hasContent = false

        for (type in contentTypes) {
            val title = prefs.getString("${type}_title", null)
            val desc = prefs.getString("${type}_desc", null)
            val due = prefs.getString("${type}_due", "No Due Date")

            if (!title.isNullOrEmpty()) {
                hasContent = true
                addPostCardToFeed(type, title, desc ?: "", due ?: "No Due Date")
            }
        }

        // Optional: Show an empty state message if no teacher content exists yet
        if (!hasContent) {
            val emptyView = TextView(this).apply {
                text = "No active lessons or activities posted yet."
                textSize = 14f
                setTextColor(ContextCompat.getColor(this@DashboardActivity, android.R.color.darker_gray))
                setPadding(16, 16, 16, 16)
            }
            feedLayout.addView(emptyView)
        }
    }

    private fun addPostCardToFeed(type: String, title: String, description: String, dueDate: String) {
        val feedLayout = layoutStudentFeed ?: return

        // Create a Google Classroom-style CardView programmatically for the student feed
        val cardView = CardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16)
            }
            radius = 12f
            cardElevation = 2f
            setOnClickListener {
                val intent = Intent(this@DashboardActivity, DetailActivity::class.java)
                intent.putExtra("CONTENT_TYPE", type)
                startActivity(intent)
            }
        }

        val innerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        // Badge showing content type (e.g., [Quiz] or [Lesson])
        val badgeView = TextView(this).apply {
            text = "📌 New $type"
            textSize = 12f
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(ContextCompat.getColor(this@DashboardActivity, android.R.color.holo_blue_dark))
        }

        // Title View
        val titleView = TextView(this).apply {
            text = title
            textSize = 18f
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(ContextCompat.getColor(this@DashboardActivity, android.R.color.black))
            setPadding(0, 4, 0, 4)
        }

        // Description View
        val descView = TextView(this).apply {
            text = description
            textSize = 14f
            setTextColor(ContextCompat.getColor(this@DashboardActivity, android.R.color.darker_gray))
        }

        // Due Date View
        val dueView = TextView(this).apply {
            text = "📅 Due: $dueDate"
            textSize = 12f
            setTypeface(typeface, Typeface.ITALIC)
            setTextColor(ContextCompat.getColor(this@DashboardActivity, android.R.color.holo_red_dark))
            setPadding(0, 8, 0, 0)
        }

        innerLayout.addView(badgeView)
        innerLayout.addView(titleView)
        if (description.isNotEmpty()) innerLayout.addView(descView)
        innerLayout.addView(dueView)

        cardView.addView(innerLayout)
        feedLayout.addView(cardView)
    }

    // ===============================================
    // SLIDE ANIMATION FUNCTIONS (Expand / Collapse)
    // ===============================================
    private fun expand(v: View) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val targetHeight = v.measuredHeight

        v.layoutParams.height = 1
        v.visibility = View.VISIBLE

        val anim = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                v.layoutParams.height = if (interpolatedTime == 1f)
                    LinearLayout.LayoutParams.WRAP_CONTENT
                else
                    (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean = true
        }

        anim.duration = 250
        v.startAnimation(anim)
    }

    private fun collapse(v: View) {
        val initialHeight = v.measuredHeight

        val anim = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean = true
        }

        anim.duration = 250
        v.startAnimation(anim)
    }
}
