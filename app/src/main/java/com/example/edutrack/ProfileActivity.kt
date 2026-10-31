package com.example.edutrack

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var imgProfile: ImageView
    private lateinit var btnChangePhoto: Button
    private lateinit var tvFullName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvSection: TextView

    private val PICK_IMAGE_REQUEST_CODE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile) // Make sure this matches your profile XML layout name

        imgProfile = findViewById(R.id.imgProfile)
        btnChangePhoto = findViewById(R.id.btnChangePhoto)
        tvFullName = findViewById(R.id.tvProfileFullName)
        tvEmail = findViewById(R.id.tvProfileEmail)
        tvSection = findViewById(R.id.tvProfileSection)

        val prefs = getSharedPreferences("EduTrackUserPrefs", Context.MODE_PRIVATE)

        // 1. Load existing profile details
        val fullName = prefs.getString("USER_FULL_NAME", "Jose Miguel Mansilungan")
        val email = prefs.getString("USER_EMAIL", "student@edutrack.com")
        val section = prefs.getString("USER_SECTION", "ACT2B")
        val savedImageUriString = prefs.getString("USER_PROFILE_IMAGE", null)

        tvFullName.text = fullName
        tvEmail.text = email
        tvSection.text = "Section: $section"

        // 2. Load saved profile image if it exists
        if (!savedImageUriString.isNullOrEmpty()) {
            val imageUri = Uri.parse(savedImageUriString)
            try {
                // Take persistable URI permission so the app can continue reading the image across restarts
                contentResolver.takePersistableUriPermission(
                    imageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                // Ignore if permissions are already handled or not needed for the URI type
            }
            imgProfile.setImageURI(imageUri)
        }

        // 3. Handle clicking "Change Photo" / Edit Profile Picture
        btnChangePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
        }
    }

    // 4. Capture the selected image URI and save it
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                try {
                    // Persist access permission to the selected image URI
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // Display the image in the ImageView immediately
                imgProfile.setImageURI(uri)

                // Save the image URI string to SharedPreferences so it stays updated
                val prefs = getSharedPreferences("EduTrackUserPrefs", Context.MODE_PRIVATE)
                prefs.edit().putString("USER_PROFILE_IMAGE", uri.toString()).apply()

                Toast.makeText(this, "Profile picture updated successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}