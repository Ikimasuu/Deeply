package edu.cit.deeply.screens.activesession

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.cit.deeply.databinding.ActivityActiveSessionBinding
import edu.cit.deeply.screens.postsession.PostSessionActivity

class ActiveSessionActivity : AppCompatActivity(), ActiveSessionContract.View {

    companion object {
        const val EXTRA_SESSION_ID = "extra_session_id"
    }

    private lateinit var binding: ActivityActiveSessionBinding
    private val presenter: ActiveSessionContract.Presenter = ActiveSessionPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActiveSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEndSession.setOnClickListener {
            presenter.onEndSessionClicked()
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
        val sessionId = intent.getStringExtra(EXTRA_SESSION_ID)
        if (sessionId != null) {
            presenter.onSessionStarted(sessionId)
        } else {
            showError("No session ID provided.")
            finish()
        }
    }

    override fun onStop() {
        presenter.detachView()
        super.onStop()
    }

    override fun updateElapsedTime(formatted: String) {
        binding.tvElapsedTime.text = formatted
    }

    override fun navigateToPostSession(sessionId: String) {
        val intent = Intent(this, PostSessionActivity::class.java)
        intent.putExtra(PostSessionActivity.EXTRA_SESSION_ID, sessionId)
        startActivity(intent)
        finish()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
