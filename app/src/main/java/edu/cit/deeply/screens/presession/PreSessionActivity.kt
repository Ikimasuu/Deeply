package edu.cit.deeply.screens.presession

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.cit.deeply.data.models.Activity
import edu.cit.deeply.data.models.EnergyLevel
import edu.cit.deeply.data.models.Environment
import edu.cit.deeply.databinding.ActivityPreSessionBinding
import edu.cit.deeply.screens.activesession.ActiveSessionActivity

class PreSessionActivity : AppCompatActivity(), PreSessionContract.View {

    private lateinit var binding: ActivityPreSessionBinding
    private val presenter: PreSessionContract.Presenter = PreSessionPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupEnvironmentButtons()
        setupActivityButtons()
        setupEnergyButtons()
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
    }

    override fun onStop() {
        presenter.detachView()
        super.onStop()
    }

    private fun setupEnvironmentButtons() {
        binding.btnEnvHome.setOnClickListener { presenter.onEnvironmentSelected(Environment.HOME) }
        binding.btnEnvCafe.setOnClickListener { presenter.onEnvironmentSelected(Environment.CAFE) }
        binding.btnEnvOffice.setOnClickListener { presenter.onEnvironmentSelected(Environment.OFFICE) }
        binding.btnEnvLibrary.setOnClickListener { presenter.onEnvironmentSelected(Environment.LIBRARY) }
        binding.btnEnvOtherQuiet.setOnClickListener { presenter.onEnvironmentSelected(Environment.OTHER_QUIET) }
        binding.btnEnvOtherNoisy.setOnClickListener { presenter.onEnvironmentSelected(Environment.OTHER_NOISY) }
    }

    private fun setupActivityButtons() {
        binding.btnActReading.setOnClickListener { presenter.onActivitySelected(Activity.READING) }
        binding.btnActWriting.setOnClickListener { presenter.onActivitySelected(Activity.WRITING) }
        binding.btnActCoding.setOnClickListener { presenter.onActivitySelected(Activity.CODING) }
        binding.btnActStudying.setOnClickListener { presenter.onActivitySelected(Activity.STUDYING) }
        binding.btnActDesigning.setOnClickListener { presenter.onActivitySelected(Activity.DESIGNING) }
        binding.btnActOther.setOnClickListener { presenter.onActivitySelected(Activity.OTHER) }
    }

    private fun setupEnergyButtons() {
        binding.btnEnergyLow.setOnClickListener { presenter.onEnergySelected(EnergyLevel.LOW) }
        binding.btnEnergyMedium.setOnClickListener { presenter.onEnergySelected(EnergyLevel.MEDIUM) }
        binding.btnEnergyHigh.setOnClickListener { presenter.onEnergySelected(EnergyLevel.HIGH) }
    }

    override fun showEnvironmentStep() {
        binding.viewFlipper.displayedChild = 0
    }

    override fun showActivityStep() {
        binding.viewFlipper.displayedChild = 1
    }

    override fun showEnergyStep() {
        binding.viewFlipper.displayedChild = 2
    }

    override fun navigateToActiveSession(sessionId: String) {
        val intent = Intent(this, ActiveSessionActivity::class.java)
        intent.putExtra(ActiveSessionActivity.EXTRA_SESSION_ID, sessionId)
        startActivity(intent)
        finish()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    @Deprecated("Use OnBackPressedDispatcher")
    override fun onBackPressed() {
        presenter.onBackPressed()
        if (binding.viewFlipper.displayedChild == 0) {
            super.onBackPressed()
        }
    }
}
