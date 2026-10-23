package com.example.edutrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. SharedPreferences Profile & Header Setup
        val sharedPref = getSharedPreferences("EduTrackUserPrefs", Context.MODE_PRIVATE)
        val dynamicName = sharedPref.getString("USER_FULL_NAME", "Jose Miguel Mansilungan")

        val tvWelcomeHeader = findViewById<TextView>(R.id.tvWelcomeHeader)
        val tvUserProfileDropdown = findViewById<TextView>(R.id.tvUserProfile)

        // Dynamically update UI headers with user's name
        tvWelcomeHeader?.text = "Welcome back,\n$dynamicName!"
        tvUserProfileDropdown?.text = "  👤 $dynamicName ▾"

        // 2. Navigation & Sidebar Element References
        val navDashboard = findViewById<TextView>(R.id.navDashboard)
        val layoutDashboardSubmenu = findViewById<LinearLayout>(R.id.layoutDashboardSubmenu)

        val navActivities = findViewById<TextView>(R.id.navActivities)
        val navQuizzes = findViewById<TextView>(R.id.navQuizzes)
        val navProjects = findViewById<TextView>(R.id.navProjects)

        val userProfileHeader = findViewById<TextView>(R.id.tvUserProfile)
        val profileDropdownMenu = findViewById<LinearLayout>(R.id.layoutProfileDropdown)
        val btnProfileDropdown = findViewById<TextView>(R.id.btnProfileDropdown)
        val btnDropdownLogout = findViewById<TextView>(R.id.btnDropdownLogout)
        val btnLogoutSidebar = findViewById<TextView>(R.id.btnLogout)

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
        userProfileHeader?.setOnClickListener {
            if (profileDropdownMenu != null) {
                profileDropdownMenu.isVisible = !profileDropdownMenu.isVisible
            } else {
                openDetailScreen("PROFILE")
            }
        }

        // Dropdown Profile Item Click
        btnProfileDropdown?.setOnClickListener {
            profileDropdownMenu?.visibility = View.GONE
            openDetailScreen("PROFILE")
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