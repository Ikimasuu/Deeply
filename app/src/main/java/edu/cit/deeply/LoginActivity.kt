package edu.cit.deeply

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val edittextUsername = findViewById<EditText>(R.id.edittextUsername)
        val edittextPassword = findViewById<EditText>(R.id.edittextPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)

        buttonLogin.setOnClickListener {
            Toast.makeText(
                this,
                "My message",
                Toast.LENGTH_SHORT
            ).show()
            Log.e("Deeply", "Button Clicked!")
        }

        val username = edittextUsername.toString()
        val password = edittextPassword.toString()

        if(username.isNullOrEmpty() || password.isNullOrEmpty()) {
            Toast.makeText(
                this,
                "Username or Password cannot be empty!!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}