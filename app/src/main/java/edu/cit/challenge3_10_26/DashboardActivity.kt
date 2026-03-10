package edu.cit.challenge3_10_26

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class DashboardActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val username = intent.getStringExtra("USERNAME") ?: "User"

        val textviewWelcome = findViewById<TextView>(R.id.textviewWelcome)
        val imageviewPhoto = findViewById<ImageView>(R.id.imageviewPhoto)
        val textviewProfile = findViewById<TextView>(R.id.textviewProfile)
        val textviewLogout = findViewById<TextView>(R.id.textviewLogout)

        textviewWelcome.text = "Welcome, $username!"

        textviewProfile.setOnClickListener {
            Log.e("Deeply", "Profile clicked")
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        textviewLogout.setOnClickListener {
            Log.e("Deeply", "Logout clicked")
            Toast.makeText(
                this,
                "Logged out successfully!",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}