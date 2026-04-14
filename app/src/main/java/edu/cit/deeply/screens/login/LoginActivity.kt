package edu.cit.deeply.screens.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.cit.deeply.R
import edu.cit.deeply.databinding.ActivityLoginBinding
import edu.cit.deeply.screens.dashboard.DashboardActivity

class LoginActivity : AppCompatActivity(), LoginContract.View {

    private lateinit var binding: ActivityLoginBinding
    private val presenter: LoginContract.Presenter = LoginPresenter()

    private var isSignUp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPillToggle()
        setupButtons()
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
    }

    override fun onStop() {
        presenter.detachView()
        super.onStop()
    }

    private fun setupPillToggle() {
        binding.toggleAuthMode.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            when (checkedId) {
                R.id.btnSignIn -> {
                    isSignUp = false
                    updatePillState()
                }
                R.id.btnSignUp -> {
                    isSignUp = true
                    updatePillState()
                }
            }
        }
        updatePillState()
    }

    private fun updatePillState() {
        val selectedColor = getColor(R.color.accent_orange)
        val unselectedColor = android.graphics.Color.TRANSPARENT
        val selectedTextColor = getColor(R.color.white)
        val unselectedTextColor = getColor(R.color.text_secondary)

        binding.btnSignIn.apply {
            setBackgroundColor(if (!isSignUp) selectedColor else unselectedColor)
            setTextColor(if (!isSignUp) selectedTextColor else unselectedTextColor)
        }
        binding.btnSignUp.apply {
            setBackgroundColor(if (isSignUp) selectedColor else unselectedColor)
            setTextColor(if (isSignUp) selectedTextColor else unselectedTextColor)
        }
    }

    private fun setupButtons() {
        binding.btnContinue.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            presenter.onContinueClicked(email, password, isSignUp)
        }

        binding.btnGoogleSignIn.setOnClickListener {
            presenter.onGoogleSignInClicked()
        }
    }

    override fun showLoading() {
        binding.btnContinue.visibility = View.INVISIBLE
        binding.progressLoading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.btnContinue.visibility = View.VISIBLE
        binding.progressLoading.visibility = View.GONE
    }

    override fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
