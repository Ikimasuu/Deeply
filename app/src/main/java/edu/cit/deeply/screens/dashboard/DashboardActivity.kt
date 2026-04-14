package edu.cit.deeply.screens.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.cit.deeply.databinding.ActivityDashboardBinding
import edu.cit.deeply.screens.history.HistoryActivity
import edu.cit.deeply.screens.presession.PreSessionActivity
import edu.cit.deeply.screens.profile.ProfileActivity

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStartSession.setOnClickListener {
            startActivity(Intent(this, PreSessionActivity::class.java))
        }

        binding.cardHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
