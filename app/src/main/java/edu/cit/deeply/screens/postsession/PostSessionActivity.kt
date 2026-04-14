package edu.cit.deeply.screens.postsession

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.cit.deeply.screens.dashboard.DashboardActivity
import edu.cit.deeply.databinding.ActivityPostSessionBinding

class PostSessionActivity : AppCompatActivity(), PostSessionContract.View {

    companion object {
        const val EXTRA_SESSION_ID = "extra_session_id"
    }

    private lateinit var binding: ActivityPostSessionBinding
    private val presenter: PostSessionContract.Presenter = PostSessionPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener {
            presenter.onSaveReflection(
                focusQuality = binding.seekFocusQuality.progress,
                distractionLevel = binding.seekDistractionLevel.progress,
                satisfaction = binding.seekSatisfaction.progress
            )
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
        val sessionId = intent.getStringExtra(EXTRA_SESSION_ID)
        if (sessionId != null) {
            presenter.onSessionLoaded(sessionId)
        } else {
            showError("No session ID provided.")
            finish()
        }
    }

    override fun onStop() {
        presenter.detachView()
        super.onStop()
    }

    override fun displaySessionSummary(environment: String, activity: String, duration: String) {
        binding.tvSessionSummary.text =
            getString(edu.cit.deeply.R.string.post_session_summary_format, environment, activity, duration)
    }

    override fun navigateToFinish() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
