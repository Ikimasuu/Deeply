package edu.cit.deeply

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class LoginActivity : Activity() {
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_login)

        val edittextUsername = findViewById<EditText>(R.id.edittextUsername)
        val edittextPassword = findViewById<EditText>(R.id.edittextPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val textviewRegister = findViewById<TextView>(R.id.textviewRegister)

        buttonLogin.setOnClickListener {
            val username = edittextUsername.text.toString()
            val password = edittextPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Username or Password cannot be empty!",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("Deeply", "Login failed: empty fields")
            } else {
                Log.e("Deeply", "Login button clicked")
                val intent = Intent(this, DashboardActivity::class.java)
                intent.putExtra("USERNAME", username)
                intent.putExtra("PASSWORD", password)
                startActivity(intent)
            }
        }

        textviewRegister.setOnClickListener {
            Log.e("Deeply", "Register Now clicked")
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}