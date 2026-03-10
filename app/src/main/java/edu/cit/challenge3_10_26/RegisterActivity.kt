package edu.cit.challenge3_10_26

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class RegisterActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val edittextUsername = findViewById<EditText>(R.id.edittextUsername)
        val edittextPassword = findViewById<EditText>(R.id.edittextPassword)
        val edittextConfirmPassword = findViewById<EditText>(R.id.edittextConfirmPassword)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val textviewLogin = findViewById<TextView>(R.id.textviewLogin)

        buttonRegister.setOnClickListener {
            val username = edittextUsername.text.toString()
            val password = edittextPassword.text.toString()
            val confirmPassword = edittextConfirmPassword.text.toString()

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(
                    this,
                    "All fields are required!",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("Deeply", "Register failed: empty fields")
            } else if (password != confirmPassword) {
                Toast.makeText(
                    this,
                    "Passwords do not match!",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("Deeply", "Register failed: passwords do not match")
            } else {
                Log.e("Deeply", "Register button clicked")
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        textviewLogin.setOnClickListener {
            Log.e("Deeply", "Login now clicked")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}