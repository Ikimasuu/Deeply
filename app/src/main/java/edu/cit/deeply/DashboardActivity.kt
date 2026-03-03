package edu.cit.deeply

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast

class DashboardActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val textviewWelcome = findViewById<TextView>(R.id.textviewWelcome)
        val textviewProfile = findViewById<TextView>(R.id.textviewProfile)
        val textviewLogout = findViewById<TextView>(R.id.textviewLogout)

        textviewWelcome.text = "Welcome to the Dashboard!"

        textviewProfile.setOnClickListener {
            Log.e("Deeply", "Profile clicked")
            val intent = Intent(this, ProfileActivity::class.java)
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