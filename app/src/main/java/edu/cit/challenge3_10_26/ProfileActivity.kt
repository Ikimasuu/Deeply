package edu.cit.challenge3_10_26

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView

class ProfileActivity : Activity() {
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_profile)

        val username = intent.getStringExtra("USERNAME") ?: "Unknown"

        val imageviewPhoto = findViewById<ImageView>(R.id.imageviewPhoto)
        val textviewUsernameLabel = findViewById<TextView>(R.id.textviewUsernameLabel)
        val textviewUsername = findViewById<TextView>(R.id.textviewUsername)
        val textviewBackToDashboard = findViewById<TextView>(R.id.textviewBackToDashboard)

        textviewUsernameLabel.text = "Username"
        textviewUsername.text = username

        textviewBackToDashboard.setOnClickListener {
            Log.e("Deeply", "Back to Dashboard clicked")
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
            finish()
        }
    }
}