package edu.cit.deeply.screens.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.cit.deeply.data.DemoDataSeeder
import edu.cit.deeply.databinding.ActivityDashboardBinding
import edu.cit.deeply.screens.history.HistoryActivity
import edu.cit.deeply.screens.presession.PreSessionActivity
import edu.cit.deeply.screens.profile.ProfileActivity

class DashboardActivity : AppCompatActivity(), DashboardContract.View {

    private lateinit var binding: ActivityDashboardBinding
    private val presenter: DashboardContract.Presenter = DashboardPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DemoDataSeeder.seed()
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStartSession.setOnClickListener { presenter.onStartSessionClicked() }
        binding.cardHistory.setOnClickListener { presenter.onHistoryClicked() }
        binding.btnProfile.setOnClickListener { presenter.onProfileClicked() }
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
        presenter.onScreenOpened()
    }

    override fun onStop() {
        presenter.detachView()
        super.onStop()
    }

    override fun displayGreeting(name: String) {
        binding.tvGreeting.text = "Good morning,"
    }

    override fun navigateToPreSession() {
        startActivity(Intent(this, PreSessionActivity::class.java))
    }

    override fun navigateToHistory() {
        startActivity(Intent(this, HistoryActivity::class.java))
    }

    override fun navigateToProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }
}
