package edu.cit.deeply.screens.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import edu.cit.deeply.R
import edu.cit.deeply.data.models.User
import edu.cit.deeply.databinding.ActivityProfileBinding
import edu.cit.deeply.screens.login.LoginActivity

class ProfileActivity : AppCompatActivity(), ProfileContract.View {

    private lateinit var binding: ActivityProfileBinding
    private val presenter: ProfileContract.Presenter = ProfilePresenter()
    private lateinit var adapter: ProfileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
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

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = ProfileAdapter(
            onRowClicked = { rowId ->
                if (rowId == "sign_out") {
                    presenter.onSignOutClicked()
                } else {
                    presenter.onRowClicked(rowId)
                }
            },
            onToggleChanged = { rowId, isChecked ->
                presenter.onToggleChanged(rowId, isChecked)
            }
        )

        binding.rvProfileRows.layoutManager = LinearLayoutManager(this)
        binding.rvProfileRows.adapter = adapter

        adapter.submitList(buildProfileRows())
    }

    private fun buildProfileRows(): List<ProfileRow> = listOf(
        // Account
        ProfileRow.SectionHeader(getString(R.string.profile_section_account)),
        ProfileRow.Navigation(
            id = "edit_profile",
            iconRes = android.R.drawable.ic_menu_edit,
            title = getString(R.string.profile_edit_profile)
        ),
        ProfileRow.Navigation(
            id = "change_password",
            iconRes = android.R.drawable.ic_lock_idle_lock,
            title = getString(R.string.profile_change_password)
        ),
        ProfileRow.Navigation(
            id = "sign_out",
            iconRes = android.R.drawable.ic_menu_revert,
            title = getString(R.string.profile_sign_out),
            showChevron = false,
            textColorRes = R.color.destructive
        ),

        // Preferences
        ProfileRow.SectionHeader(getString(R.string.profile_section_preferences)),
        ProfileRow.Toggle(
            id = "notifications",
            iconRes = android.R.drawable.ic_popup_reminder,
            title = getString(R.string.profile_notifications),
            isChecked = true
        ),
        ProfileRow.Toggle(
            id = "haptic_feedback",
            iconRes = android.R.drawable.ic_menu_compass,
            title = getString(R.string.profile_haptic_feedback),
            isChecked = true
        ),
        ProfileRow.Navigation(
            id = "theme",
            iconRes = android.R.drawable.ic_menu_gallery,
            title = getString(R.string.profile_theme),
            subtitle = getString(R.string.profile_theme_system)
        ),

        // About
        ProfileRow.SectionHeader(getString(R.string.profile_section_about)),
        ProfileRow.Navigation(
            id = "privacy_policy",
            iconRes = android.R.drawable.ic_menu_info_details,
            title = getString(R.string.profile_privacy_policy)
        ),
        ProfileRow.Navigation(
            id = "terms_of_service",
            iconRes = android.R.drawable.ic_menu_info_details,
            title = getString(R.string.profile_terms_of_service)
        ),
        ProfileRow.Navigation(
            id = "version",
            iconRes = android.R.drawable.ic_menu_help,
            title = getString(R.string.profile_version),
            subtitle = getString(R.string.profile_version_number),
            showChevron = false
        )
    )

    override fun displayUser(user: User) {
        binding.tvUserName.text = user.name
        binding.tvUserEmail.text = user.email
    }

    override fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
