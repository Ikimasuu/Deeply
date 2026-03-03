package edu.cit.deeply

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class RegisterActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val edittextUsername = findViewById<EditText>(R.id.edittextUsername)
        val edittextPassword = findViewById<EditText>(R.id.edittextPassword)
        val edittextReenterPassword = findViewById<EditText>(R.id.edittextReenterPassword)
        val buttonSubmit = findViewById<Button>(R.id.buttonSubmit)

        buttonSubmit.setOnClickListener {
            val username = edittextUsername.text.toString()
            val password = edittextPassword.text.toString()
            val reenterPassword = edittextReenterPassword.text.toString()

            if (username.isEmpty() || password.isEmpty() || reenterPassword.isEmpty()) {
                Toast.makeText(
                    this,
                    "All fields are required!",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("Deeply", "Register failed: empty fields")
            } else if (password != reenterPassword) {
                Toast.makeText(
                    this,
                    "Passwords do not match!",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("Deeply", "Register failed: passwords do not match")
            } else {
                Log.e("Deeply", "Submit button clicked")
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}