package edu.cit.deeply

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class ProfileActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val textviewUsername = findViewById<TextView>(R.id.textviewUsername)
        val textviewFirstName = findViewById<TextView>(R.id.textviewFirstName)
        val textviewMiddleName = findViewById<TextView>(R.id.textviewMiddleName)
        val textviewLastName = findViewById<TextView>(R.id.textviewLastName)
        val textviewEmail = findViewById<TextView>(R.id.textviewEmail)
        val textviewBackToDashboard = findViewById<TextView>(R.id.textviewBackToDashboard)

        textviewUsername.text = "Username: john_doe"
        textviewFirstName.text = "First Name: John"
        textviewMiddleName.text = "Middle Name: Michael"
        textviewLastName.text = "Last Name: Doe"
        textviewEmail.text = "Email: john.doe@email.com"

        textviewBackToDashboard.setOnClickListener {
            Log.e("Deeply", "Back to Dashboard clicked")
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}