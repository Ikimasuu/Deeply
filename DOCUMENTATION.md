# Deeply - Complete Project Documentation

## Section 1: Complete Code

This section contains the full source code for the Deeply Android application, organized by Activities, Classes, XML Layouts, Resources, and Manifest.

---

### 1.1 AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Deeply"
        tools:targetApi="31">

        <!-- Launcher: Login -->
        <activity
            android:name=".screens.login.LoginActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!-- Dashboard hub -->
        <activity android:name=".screens.dashboard.DashboardActivity" />

        <!-- Session loop -->
        <activity android:name=".screens.presession.PreSessionActivity" />
        <activity android:name=".screens.activesession.ActiveSessionActivity" />
        <activity android:name=".screens.postsession.PostSessionActivity" />

        <!-- Other screens -->
        <activity android:name=".screens.profile.ProfileActivity" />
        <activity android:name=".screens.history.HistoryActivity" />

    </application>

</manifest>
```

---

### 1.2 Data Models

#### User.kt

```kotlin
package edu.cit.deeply.data.models

data class User(
    val name: String,
    val email: String
)
```

#### Session.kt

```kotlin
package edu.cit.deeply.data.models

data class Session(
    val id: String,
    val environment: Environment,
    val activity: Activity,
    val energy: EnergyLevel,
    val startTime: Long,
    var endTime: Long? = null,
    var focusQuality: Int? = null,
    var distractionLevel: Int? = null,
    var satisfaction: Int? = null
)
```

#### Activity.kt

```kotlin
package edu.cit.deeply.data.models

enum class Activity {
    READING,
    WRITING,
    CODING,
    STUDYING,
    DESIGNING,
    OTHER
}
```

#### Environment.kt

```kotlin
package edu.cit.deeply.data.models

enum class Environment {
    HOME,
    CAFE,
    OFFICE,
    LIBRARY,
    OTHER_QUIET,
    OTHER_NOISY
}
```

#### EnergyLevel.kt

```kotlin
package edu.cit.deeply.data.models

enum class EnergyLevel {
    LOW,
    MEDIUM,
    HIGH
}
```

---

### 1.3 Repositories

#### AuthRepository.kt

```kotlin
package edu.cit.deeply.data.repositories

import edu.cit.deeply.data.models.User
import kotlinx.coroutines.delay

object AuthRepository {

    private var currentUser: User? = null

    suspend fun signIn(email: String, password: String): Result<Unit> {
        delay(500L)
        currentUser = User(name = "Jane Doe", email = email)
        return Result.success(Unit)
    }

    suspend fun signUp(email: String, password: String): Result<Unit> {
        delay(500L)
        currentUser = User(name = "Jane Doe", email = email)
        return Result.success(Unit)
    }

    suspend fun signInWithGoogle(): Result<Unit> {
        delay(500L)
        currentUser = User(name = "Jane Doe", email = "jane@gmail.com")
        return Result.success(Unit)
    }

    fun getCurrentUser(): User {
        return currentUser ?: User(name = "Jane Doe", email = "jane@example.com")
    }

    fun signOut() {
        currentUser = null
    }
}
```

#### SessionRepository.kt

```kotlin
package edu.cit.deeply.data.repositories

import edu.cit.deeply.data.models.Session

object SessionRepository {

    private val sessions = mutableListOf<Session>()

    fun startSession(session: Session) {
        sessions.add(session)
    }

    fun getSession(id: String): Session? {
        return sessions.find { it.id == id }
    }

    fun updateSession(id: String, update: (Session) -> Unit) {
        sessions.find { it.id == id }?.let(update)
    }

    fun getAllSessions(): List<Session> = sessions.toList()
}
```

---

### 1.4 Login Screen (MVP)

#### LoginContract.kt

```kotlin
package edu.cit.deeply.screens.login

interface LoginContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun navigateToDashboard()
        fun showError(message: String)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun onContinueClicked(email: String, password: String, isSignUp: Boolean)
        fun onGoogleSignInClicked()
    }
}
```

#### LoginPresenter.kt

```kotlin
package edu.cit.deeply.screens.login

import edu.cit.deeply.data.repositories.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LoginPresenter : LoginContract.Presenter {

    private var view: LoginContract.View? = null
    private var scope: CoroutineScope? = null

    override fun attachView(view: LoginContract.View) {
        this.view = view
        scope = CoroutineScope(Dispatchers.Main + Job())
    }

    override fun detachView() {
        scope?.cancel()
        scope = null
        this.view = null
    }

    override fun onContinueClicked(email: String, password: String, isSignUp: Boolean) {
        if (email.isBlank() || password.isBlank()) {
            view?.showError("Please enter your email and password.")
            return
        }

        view?.showLoading()
        scope?.launch {
            val result = if (isSignUp) {
                AuthRepository.signUp(email, password)
            } else {
                AuthRepository.signIn(email, password)
            }
            view?.hideLoading()
            result.fold(
                onSuccess = {
                    view?.navigateToDashboard()
                },
                onFailure = { e ->
                    view?.showError(e.message ?: "Authentication failed.")
                }
            )
        }
    }

    override fun onGoogleSignInClicked() {
        view?.showLoading()
        scope?.launch {
            val result = AuthRepository.signInWithGoogle()
            view?.hideLoading()
            result.fold(
                onSuccess = {
                    view?.navigateToDashboard()
                },
                onFailure = { e ->
                    view?.showError(e.message ?: "Google sign-in failed.")
                }
            )
        }
    }
}
```

#### LoginActivity.kt

```kotlin
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
```

---

### 1.5 Pre-Session Screen (MVP)

#### PreSessionContract.kt

```kotlin
package edu.cit.deeply.screens.presession

import edu.cit.deeply.data.models.Activity
import edu.cit.deeply.data.models.EnergyLevel
import edu.cit.deeply.data.models.Environment

interface PreSessionContract {

    interface View {
        fun showEnvironmentStep()
        fun showActivityStep()
        fun showEnergyStep()
        fun navigateToActiveSession(sessionId: String)
        fun showError(message: String)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun onEnvironmentSelected(env: Environment)
        fun onActivitySelected(activity: Activity)
        fun onEnergySelected(energy: EnergyLevel)
        fun onBackPressed()
    }
}
```

#### PreSessionPresenter.kt

```kotlin
package edu.cit.deeply.screens.presession

import edu.cit.deeply.data.models.Activity
import edu.cit.deeply.data.models.EnergyLevel
import edu.cit.deeply.data.models.Environment
import edu.cit.deeply.data.models.Session
import edu.cit.deeply.data.repositories.SessionRepository
import java.util.UUID

class PreSessionPresenter : PreSessionContract.Presenter {

    private var view: PreSessionContract.View? = null

    private var selectedEnvironment: Environment? = null
    private var selectedActivity: Activity? = null

    override fun attachView(view: PreSessionContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun onEnvironmentSelected(env: Environment) {
        selectedEnvironment = env
        view?.showActivityStep()
    }

    override fun onActivitySelected(activity: Activity) {
        selectedActivity = activity
        view?.showEnergyStep()
    }

    override fun onEnergySelected(energy: EnergyLevel) {
        val env = selectedEnvironment
        val act = selectedActivity

        if (env == null || act == null) {
            view?.showError("Please complete all steps.")
            return
        }

        val session = Session(
            id = UUID.randomUUID().toString(),
            environment = env,
            activity = act,
            energy = energy,
            startTime = System.currentTimeMillis()
        )

        SessionRepository.startSession(session)
        view?.navigateToActiveSession(session.id)
    }

    override fun onBackPressed() {
        when {
            selectedActivity != null -> {
                selectedActivity = null
                view?.showActivityStep()
            }
            selectedEnvironment != null -> {
                selectedEnvironment = null
                view?.showEnvironmentStep()
            }
        }
    }
}
```

#### PreSessionActivity.kt

```kotlin
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
```

---

### 1.6 Active Session Screen (MVP)

#### ActiveSessionContract.kt

```kotlin
package edu.cit.deeply.screens.activesession

interface ActiveSessionContract {

    interface View {
        fun updateElapsedTime(formatted: String)
        fun navigateToPostSession(sessionId: String)
        fun showError(message: String)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun onSessionStarted(sessionId: String)
        fun onEndSessionClicked()
    }
}
```

#### ActiveSessionPresenter.kt

```kotlin
package edu.cit.deeply.screens.activesession

import edu.cit.deeply.data.repositories.SessionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ActiveSessionPresenter : ActiveSessionContract.Presenter {

    private var view: ActiveSessionContract.View? = null
    private var sessionId: String? = null
    private var scope: CoroutineScope? = null
    private var startTime: Long = 0L

    override fun attachView(view: ActiveSessionContract.View) {
        this.view = view
        scope = CoroutineScope(Dispatchers.Main + Job())
        sessionId?.let { startTimer(it) }
    }

    override fun detachView() {
        scope?.cancel()
        scope = null
        this.view = null
    }

    override fun onSessionStarted(sessionId: String) {
        this.sessionId = sessionId
        val session = SessionRepository.getSession(sessionId)
        if (session == null) {
            view?.showError("Session not found.")
            return
        }
        startTime = session.startTime
        startTimer(sessionId)
    }

    override fun onEndSessionClicked() {
        val id = sessionId ?: return
        SessionRepository.updateSession(id) { session ->
            session.endTime = System.currentTimeMillis()
        }
        scope?.cancel()
        view?.navigateToPostSession(id)
    }

    private fun startTimer(sessionId: String) {
        scope?.launch {
            while (true) {
                val elapsed = System.currentTimeMillis() - startTime
                val totalSeconds = elapsed / 1000
                val minutes = totalSeconds / 60
                val seconds = totalSeconds % 60
                val formatted = String.format("%02d:%02d", minutes, seconds)
                view?.updateElapsedTime(formatted)
                delay(1000L)
            }
        }
    }
}
```

#### ActiveSessionActivity.kt

```kotlin
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
```

---

### 1.7 Post-Session Screen (MVP)

#### PostSessionContract.kt

```kotlin
package edu.cit.deeply.screens.postsession

interface PostSessionContract {

    interface View {
        fun displaySessionSummary(environment: String, activity: String, duration: String)
        fun navigateToFinish()
        fun showError(message: String)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun onSessionLoaded(sessionId: String)
        fun onSaveReflection(focusQuality: Int, distractionLevel: Int, satisfaction: Int)
    }
}
```

#### PostSessionPresenter.kt

```kotlin
package edu.cit.deeply.screens.postsession

import edu.cit.deeply.data.repositories.SessionRepository

class PostSessionPresenter : PostSessionContract.Presenter {

    private var view: PostSessionContract.View? = null
    private var sessionId: String? = null

    override fun attachView(view: PostSessionContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun onSessionLoaded(sessionId: String) {
        this.sessionId = sessionId
        val session = SessionRepository.getSession(sessionId)
        if (session == null) {
            view?.showError("Session not found.")
            return
        }

        val durationMs = (session.endTime ?: System.currentTimeMillis()) - session.startTime
        val totalSeconds = durationMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val duration = String.format("%02d:%02d", minutes, seconds)

        view?.displaySessionSummary(
            environment = session.environment.name,
            activity = session.activity.name,
            duration = duration
        )
    }

    override fun onSaveReflection(focusQuality: Int, distractionLevel: Int, satisfaction: Int) {
        val id = sessionId ?: return
        SessionRepository.updateSession(id) { session ->
            session.focusQuality = focusQuality
            session.distractionLevel = distractionLevel
            session.satisfaction = satisfaction
        }
        view?.navigateToFinish()
    }
}
```

#### PostSessionActivity.kt

```kotlin
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
```

---

### 1.8 Profile Screen (MVP)

#### ProfileContract.kt

```kotlin
package edu.cit.deeply.screens.profile

import edu.cit.deeply.data.models.User

interface ProfileContract {

    interface View {
        fun displayUser(user: User)
        fun navigateToLogin()
        fun showError(message: String)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun onScreenOpened()
        fun onSignOutClicked()
        fun onRowClicked(rowId: String)
        fun onToggleChanged(rowId: String, isChecked: Boolean)
    }
}
```

#### ProfilePresenter.kt

```kotlin
package edu.cit.deeply.screens.profile

import android.util.Log
import edu.cit.deeply.data.repositories.AuthRepository

class ProfilePresenter : ProfileContract.Presenter {

    private var view: ProfileContract.View? = null

    override fun attachView(view: ProfileContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun onScreenOpened() {
        val user = AuthRepository.getCurrentUser()
        view?.displayUser(user)
    }

    override fun onSignOutClicked() {
        AuthRepository.signOut()
        view?.navigateToLogin()
    }

    override fun onRowClicked(rowId: String) {
        Log.d("ProfilePresenter", "Row clicked: $rowId")
    }

    override fun onToggleChanged(rowId: String, isChecked: Boolean) {
        Log.d("ProfilePresenter", "Toggle changed: $rowId = $isChecked")
    }
}
```

#### ProfileAdapter.kt

```kotlin
package edu.cit.deeply.screens.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.materialswitch.MaterialSwitch
import edu.cit.deeply.R

sealed class ProfileRow {
    data class SectionHeader(val title: String) : ProfileRow()
    data class Navigation(
        val id: String,
        val iconRes: Int,
        val title: String,
        val subtitle: String? = null,
        val showChevron: Boolean = true,
        val textColorRes: Int = R.color.text_primary
    ) : ProfileRow()
    data class Toggle(
        val id: String,
        val iconRes: Int,
        val title: String,
        val isChecked: Boolean = false
    ) : ProfileRow()
}

class ProfileAdapter(
    private val onRowClicked: (String) -> Unit,
    private val onToggleChanged: (String, Boolean) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_SECTION_HEADER = 0
        private const val TYPE_NAVIGATION = 1
        private const val TYPE_TOGGLE = 2
    }

    private val items = mutableListOf<ProfileRow>()

    fun submitList(rows: List<ProfileRow>) {
        items.clear()
        items.addAll(rows)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is ProfileRow.SectionHeader -> TYPE_SECTION_HEADER
        is ProfileRow.Navigation -> TYPE_NAVIGATION
        is ProfileRow.Toggle -> TYPE_TOGGLE
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_SECTION_HEADER -> SectionHeaderViewHolder(
                inflater.inflate(R.layout.item_profile_section_header, parent, false)
            )
            TYPE_NAVIGATION -> NavigationViewHolder(
                inflater.inflate(R.layout.item_profile_navigation, parent, false)
            )
            TYPE_TOGGLE -> ToggleViewHolder(
                inflater.inflate(R.layout.item_profile_toggle, parent, false)
            )
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ProfileRow.SectionHeader -> (holder as SectionHeaderViewHolder).bind(item)
            is ProfileRow.Navigation -> (holder as NavigationViewHolder).bind(item)
            is ProfileRow.Toggle -> (holder as ToggleViewHolder).bind(item)
        }
    }

    inner class SectionHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvTitle: TextView = view.findViewById(R.id.tvSectionTitle)

        fun bind(item: ProfileRow.SectionHeader) {
            tvTitle.text = item.title
        }
    }

    inner class NavigationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
        private val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        private val tvSubtitle: TextView = view.findViewById(R.id.tvSubtitle)
        private val ivChevron: ImageView = view.findViewById(R.id.ivChevron)

        fun bind(item: ProfileRow.Navigation) {
            ivIcon.setImageResource(item.iconRes)
            tvTitle.text = item.title
            tvTitle.setTextColor(itemView.context.getColor(item.textColorRes))

            if (item.subtitle != null) {
                tvSubtitle.text = item.subtitle
                tvSubtitle.visibility = View.VISIBLE
            } else {
                tvSubtitle.visibility = View.GONE
            }

            ivChevron.visibility = if (item.showChevron) View.VISIBLE else View.GONE

            itemView.setOnClickListener { onRowClicked(item.id) }
        }
    }

    inner class ToggleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
        private val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        private val switchToggle: MaterialSwitch = view.findViewById(R.id.switchToggle)

        fun bind(item: ProfileRow.Toggle) {
            ivIcon.setImageResource(item.iconRes)
            tvTitle.text = item.title
            switchToggle.isChecked = item.isChecked
            switchToggle.setOnCheckedChangeListener { _, isChecked ->
                onToggleChanged(item.id, isChecked)
            }
        }
    }
}
```

#### ProfileActivity.kt

```kotlin
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
```

---

### 1.9 History Screen (MVP)

#### HistoryContract.kt

```kotlin
package edu.cit.deeply.screens.history

interface HistoryContract {

    interface View {
        fun displaySessions(items: List<HistoryItem>)
        fun showEmptyState()
        fun showError(message: String)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun onScreenOpened()
        fun onSessionClicked(position: Int)
    }
}

sealed class HistoryItem {
    data class DateHeader(val label: String) : HistoryItem()
    data class SessionCard(
        val sessionId: String,
        val activityType: String,
        val environment: String,
        val duration: String,
        val startTime: String,
        val focusQuality: Int?,
        val distractionLevel: Int?,
        val satisfaction: Int?,
        val energyLevel: String,
        val fullStartTime: String,
        val fullEndTime: String,
        var isExpanded: Boolean = false
    ) : HistoryItem()
}
```

#### HistoryPresenter.kt

```kotlin
package edu.cit.deeply.screens.history

import edu.cit.deeply.data.models.Session
import edu.cit.deeply.data.repositories.SessionRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HistoryPresenter : HistoryContract.Presenter {

    private var view: HistoryContract.View? = null
    private var items: MutableList<HistoryItem> = mutableListOf()

    override fun attachView(view: HistoryContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun onScreenOpened() {
        val sessions = SessionRepository.getAllSessions()

        if (sessions.isEmpty()) {
            view?.showEmptyState()
            return
        }

        items = buildGroupedList(sessions)
        view?.displaySessions(items)
    }

    override fun onSessionClicked(position: Int) {
        val item = items.getOrNull(position) as? HistoryItem.SessionCard ?: return
        item.isExpanded = !item.isExpanded
        view?.displaySessions(items)
    }

    private fun buildGroupedList(sessions: List<Session>): MutableList<HistoryItem> {
        val sorted = sessions.sortedByDescending { it.startTime }
        val result = mutableListOf<HistoryItem>()
        var lastDateLabel: String? = null

        for (session in sorted) {
            val dateLabel = formatDateLabel(session.startTime)
            if (dateLabel != lastDateLabel) {
                result.add(HistoryItem.DateHeader(dateLabel))
                lastDateLabel = dateLabel
            }
            result.add(sessionToCard(session))
        }

        return result
    }

    private fun formatDateLabel(timestamp: Long): String {
        val sessionCal = Calendar.getInstance().apply { timeInMillis = timestamp }
        val todayCal = Calendar.getInstance()

        return when {
            isSameDay(sessionCal, todayCal) -> "Today"
            isSameDay(sessionCal, todayCal.apply { add(Calendar.DAY_OF_YEAR, -1) }) -> "Yesterday"
            else -> SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date(timestamp))
        }
    }

    private fun isSameDay(a: Calendar, b: Calendar): Boolean {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
                a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
    }

    private fun sessionToCard(session: Session): HistoryItem.SessionCard {
        val durationMs = (session.endTime ?: System.currentTimeMillis()) - session.startTime
        val totalSeconds = durationMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val duration = String.format("%02d:%02d", minutes, seconds)

        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val dateTimeFormat = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault())

        return HistoryItem.SessionCard(
            sessionId = session.id,
            activityType = formatEnumName(session.activity.name),
            environment = formatEnumName(session.environment.name),
            duration = duration,
            startTime = timeFormat.format(Date(session.startTime)),
            focusQuality = session.focusQuality,
            distractionLevel = session.distractionLevel,
            satisfaction = session.satisfaction,
            energyLevel = formatEnumName(session.energy.name),
            fullStartTime = dateTimeFormat.format(Date(session.startTime)),
            fullEndTime = session.endTime?.let { dateTimeFormat.format(Date(it)) } ?: "In progress"
        )
    }

    private fun formatEnumName(name: String): String {
        return name.replace("_", " ").lowercase()
            .replaceFirstChar { it.uppercase() }
    }
}
```

#### HistoryAdapter.kt

```kotlin
package edu.cit.deeply.screens.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.cit.deeply.R

class HistoryAdapter(
    private val onSessionClicked: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_DATE_HEADER = 0
        private const val TYPE_SESSION_CARD = 1
    }

    private val items = mutableListOf<HistoryItem>()

    fun submitList(newItems: List<HistoryItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is HistoryItem.DateHeader -> TYPE_DATE_HEADER
        is HistoryItem.SessionCard -> TYPE_SESSION_CARD
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_DATE_HEADER -> DateHeaderViewHolder(
                inflater.inflate(R.layout.item_date_header, parent, false)
            )
            TYPE_SESSION_CARD -> SessionCardViewHolder(
                inflater.inflate(R.layout.item_session_card, parent, false)
            )
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is HistoryItem.DateHeader -> (holder as DateHeaderViewHolder).bind(item)
            is HistoryItem.SessionCard -> (holder as SessionCardViewHolder).bind(item, position)
        }
    }

    inner class DateHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvDateHeader: TextView = view.findViewById(R.id.tvDateHeader)

        fun bind(item: HistoryItem.DateHeader) {
            tvDateHeader.text = item.label
        }
    }

    inner class SessionCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvActivityType: TextView = view.findViewById(R.id.tvActivityType)
        private val tvEnvironmentDuration: TextView = view.findViewById(R.id.tvEnvironmentDuration)
        private val tvStartTime: TextView = view.findViewById(R.id.tvStartTime)
        private val layoutMetrics: LinearLayout = view.findViewById(R.id.layoutMetrics)
        private val tvMetricFocus: TextView = view.findViewById(R.id.tvMetricFocus)
        private val tvMetricDistraction: TextView = view.findViewById(R.id.tvMetricDistraction)
        private val tvMetricSatisfaction: TextView = view.findViewById(R.id.tvMetricSatisfaction)
        private val layoutExpanded: LinearLayout = view.findViewById(R.id.layoutExpanded)
        private val tvEnergyLevel: TextView = view.findViewById(R.id.tvEnergyLevel)
        private val tvFullStartTime: TextView = view.findViewById(R.id.tvFullStartTime)
        private val tvFullEndTime: TextView = view.findViewById(R.id.tvFullEndTime)
        private val layoutExpandedScores: LinearLayout = view.findViewById(R.id.layoutExpandedScores)
        private val tvDetailFocus: TextView = view.findViewById(R.id.tvDetailFocus)
        private val tvDetailDistraction: TextView = view.findViewById(R.id.tvDetailDistraction)
        private val tvDetailSatisfaction: TextView = view.findViewById(R.id.tvDetailSatisfaction)

        fun bind(item: HistoryItem.SessionCard, position: Int) {
            val ctx = itemView.context

            tvActivityType.text = item.activityType
            tvEnvironmentDuration.text = ctx.getString(
                R.string.post_session_summary_format,
                item.environment,
                item.duration,
                ""
            ).trimEnd(' ', '\u00B7').trim()
            tvStartTime.text = item.startTime

            val hasScores = item.focusQuality != null
            if (hasScores) {
                layoutMetrics.visibility = View.VISIBLE
                tvMetricFocus.text = ctx.getString(R.string.history_focus) + " " + item.focusQuality
                tvMetricDistraction.text = ctx.getString(R.string.history_distraction) + " " + item.distractionLevel
                tvMetricSatisfaction.text = ctx.getString(R.string.history_satisfaction) + " " + item.satisfaction
            } else {
                layoutMetrics.visibility = View.GONE
            }

            layoutExpanded.visibility = if (item.isExpanded) View.VISIBLE else View.GONE

            if (item.isExpanded) {
                tvEnergyLevel.text = ctx.getString(R.string.history_energy) + ": " + item.energyLevel
                tvFullStartTime.text = ctx.getString(R.string.history_started) + ": " + item.fullStartTime
                tvFullEndTime.text = ctx.getString(R.string.history_ended) + ": " + item.fullEndTime

                if (hasScores) {
                    layoutExpandedScores.visibility = View.VISIBLE
                    tvDetailFocus.text = ctx.getString(R.string.post_session_focus_quality) + ": " + item.focusQuality + "/100"
                    tvDetailDistraction.text = ctx.getString(R.string.post_session_distraction) + ": " + item.distractionLevel + "/100"
                    tvDetailSatisfaction.text = ctx.getString(R.string.post_session_satisfaction) + ": " + item.satisfaction + "/100"
                } else {
                    layoutExpandedScores.visibility = View.GONE
                }
            }

            itemView.setOnClickListener { onSessionClicked(position) }
        }
    }
}
```

#### HistoryActivity.kt

```kotlin
package edu.cit.deeply.screens.history

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import edu.cit.deeply.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity(), HistoryContract.View {

    private lateinit var binding: ActivityHistoryBinding
    private val presenter: HistoryContract.Presenter = HistoryPresenter()
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
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
        adapter = HistoryAdapter(
            onSessionClicked = { position -> presenter.onSessionClicked(position) }
        )
        binding.rvSessions.layoutManager = LinearLayoutManager(this)
        binding.rvSessions.adapter = adapter
    }

    override fun displaySessions(items: List<HistoryItem>) {
        binding.rvSessions.visibility = View.VISIBLE
        binding.layoutEmpty.visibility = View.GONE
        adapter.submitList(items)
    }

    override fun showEmptyState() {
        binding.rvSessions.visibility = View.GONE
        binding.layoutEmpty.visibility = View.VISIBLE
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
```

---

### 1.10 XML Layouts

#### activity_login.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/background_warm"
    android:fillViewport="true">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:gravity="center_horizontal"
        android:paddingHorizontal="@dimen/space_xl"
        android:paddingTop="@dimen/space_3xl"
        android:paddingBottom="@dimen/space_xl">

        <ImageView
            android:id="@+id/ivLogo"
            android:layout_width="@dimen/logo_size"
            android:layout_height="@dimen/logo_size"
            android:src="@drawable/ic_logo_placeholder"
            android:contentDescription="@string/app_name"
            android:layout_marginBottom="@dimen/space_3xl" />

        <com.google.android.material.button.MaterialButtonToggleGroup
            android:id="@+id/toggleAuthMode"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:background="@drawable/bg_pill_toggle"
            android:padding="@dimen/space_xs"
            app:singleSelection="true"
            app:selectionRequired="true"
            app:checkedButton="@id/btnSignIn"
            android:layout_marginBottom="@dimen/space_2xl">

            <com.google.android.material.button.MaterialButton
                android:id="@+id/btnSignIn"
                style="@style/Widget.Deeply.Button.PillTab"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="@string/login_sign_in" />

            <com.google.android.material.button.MaterialButton
                android:id="@+id/btnSignUp"
                style="@style/Widget.Deeply.Button.PillTab"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="@string/login_sign_up" />

        </com.google.android.material.button.MaterialButtonToggleGroup>

        <com.google.android.material.textfield.TextInputLayout
            android:id="@+id/tilEmail"
            style="@style/Widget.Deeply.TextInputLayout"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="@string/login_email_hint"
            android:layout_marginBottom="@dimen/space_md">

            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/etEmail"
                android:layout_width="match_parent"
                android:layout_height="@dimen/height_input"
                android:inputType="textEmailAddress"
                android:textAppearance="@style/TextAppearance.Deeply.Body" />

        </com.google.android.material.textfield.TextInputLayout>

        <com.google.android.material.textfield.TextInputLayout
            android:id="@+id/tilPassword"
            style="@style/Widget.Deeply.TextInputLayout"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="@string/login_password_hint"
            app:endIconMode="password_toggle"
            android:layout_marginBottom="@dimen/space_xl">

            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/etPassword"
                android:layout_width="match_parent"
                android:layout_height="@dimen/height_input"
                android:inputType="textPassword"
                android:textAppearance="@style/TextAppearance.Deeply.Body" />

        </com.google.android.material.textfield.TextInputLayout>

        <com.google.android.material.button.MaterialButton
            android:id="@+id/btnContinue"
            style="@style/Widget.Deeply.Button.Primary"
            android:layout_width="match_parent"
            android:layout_height="@dimen/height_button"
            android:text="@string/login_continue"
            android:layout_marginBottom="@dimen/space_xl" />

        <com.google.android.material.progressindicator.CircularProgressIndicator
            android:id="@+id/progressLoading"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:indeterminate="true"
            android:visibility="gone"
            app:indicatorColor="@color/accent_orange"
            android:layout_marginBottom="@dimen/space_xl" />

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="center_vertical"
            android:layout_marginBottom="@dimen/space_xl">

            <View
                android:layout_width="0dp"
                android:layout_height="1dp"
                android:layout_weight="1"
                android:background="@color/divider" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="@string/login_divider_or"
                android:textAppearance="@style/TextAppearance.Deeply.Caption"
                android:paddingHorizontal="@dimen/space_lg" />

            <View
                android:layout_width="0dp"
                android:layout_height="1dp"
                android:layout_weight="1"
                android:background="@color/divider" />

        </LinearLayout>

        <LinearLayout
            android:id="@+id/btnGoogleSignIn"
            android:layout_width="match_parent"
            android:layout_height="@dimen/height_button"
            android:orientation="horizontal"
            android:gravity="center"
            android:background="@drawable/bg_google_button"
            android:clickable="true"
            android:focusable="true"
            android:foreground="?android:attr/selectableItemBackground">

            <ImageView
                android:layout_width="@dimen/icon_size"
                android:layout_height="@dimen/icon_size"
                android:src="@drawable/ic_google"
                android:contentDescription="@string/login_google"
                android:layout_marginEnd="@dimen/space_md" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="@string/login_google"
                android:textAppearance="@style/TextAppearance.Deeply.Button"
                android:textColor="@color/text_primary" />

        </LinearLayout>

    </LinearLayout>
</ScrollView>
```

#### activity_pre_session.xml, activity_active_session.xml, activity_post_session.xml, activity_profile.xml, activity_history.xml

*(The remaining activity layouts, item layouts, drawable XMLs, and resource value files follow the same patterns shown above. They are included in the project source tree under `app/src/main/res/`.)*

---

### 1.11 Resource Values

#### colors.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>

    <color name="accent_orange">#E8622A</color>
    <color name="background_warm">#FAF8F5</color>

    <color name="accent">@color/accent_orange</color>
    <color name="background">@color/background_warm</color>

    <color name="grey_900">#1A1A1A</color>
    <color name="grey_800">#333333</color>
    <color name="grey_700">#4D4D4D</color>
    <color name="grey_600">#666666</color>
    <color name="grey_500">#808080</color>
    <color name="grey_400">#999999</color>
    <color name="grey_300">#B3B3B3</color>
    <color name="grey_200">#CCCCCC</color>
    <color name="grey_100">#E6E6E6</color>

    <color name="text_primary">@color/grey_900</color>
    <color name="text_secondary">@color/grey_600</color>
    <color name="text_tertiary">@color/grey_400</color>

    <color name="surface">@color/white</color>
    <color name="surface_elevated">@color/white</color>
    <color name="divider">@color/grey_200</color>

    <color name="destructive">#D14545</color>
</resources>
```

#### dimens.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <dimen name="screen_padding">24dp</dimen>
    <dimen name="button_spacing">12dp</dimen>

    <dimen name="space_xs">4dp</dimen>
    <dimen name="space_sm">8dp</dimen>
    <dimen name="space_md">12dp</dimen>
    <dimen name="space_lg">16dp</dimen>
    <dimen name="space_xl">24dp</dimen>
    <dimen name="space_2xl">32dp</dimen>
    <dimen name="space_3xl">48dp</dimen>

    <dimen name="radius_sm">8dp</dimen>
    <dimen name="radius_md">12dp</dimen>
    <dimen name="radius_lg">16dp</dimen>
    <dimen name="radius_pill">999dp</dimen>

    <dimen name="height_button">56dp</dimen>
    <dimen name="height_input">56dp</dimen>
    <dimen name="height_row">64dp</dimen>

    <dimen name="avatar_size">80dp</dimen>
    <dimen name="icon_size">24dp</dimen>
    <dimen name="logo_size">64dp</dimen>
    <dimen name="metric_badge_size">28dp</dimen>
</resources>
```

#### styles.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <style name="TextAppearance.Deeply.Display" parent="TextAppearance.Material3.DisplaySmall">
        <item name="fontFamily">serif</item>
        <item name="android:fontFamily">serif</item>
        <item name="android:textSize">32sp</item>
        <item name="android:textColor">@color/text_primary</item>
    </style>

    <style name="TextAppearance.Deeply.Heading1" parent="TextAppearance.Material3.HeadlineMedium">
        <item name="fontFamily">sans-serif-medium</item>
        <item name="android:fontFamily">sans-serif-medium</item>
        <item name="android:textSize">24sp</item>
        <item name="android:textColor">@color/text_primary</item>
    </style>

    <style name="TextAppearance.Deeply.Heading2" parent="TextAppearance.Material3.HeadlineSmall">
        <item name="fontFamily">sans-serif-medium</item>
        <item name="android:fontFamily">sans-serif-medium</item>
        <item name="android:textSize">20sp</item>
        <item name="android:textColor">@color/text_primary</item>
    </style>

    <style name="TextAppearance.Deeply.Body" parent="TextAppearance.Material3.BodyLarge">
        <item name="fontFamily">sans-serif</item>
        <item name="android:fontFamily">sans-serif</item>
        <item name="android:textSize">16sp</item>
        <item name="android:textColor">@color/text_primary</item>
    </style>

    <style name="TextAppearance.Deeply.BodySmall" parent="TextAppearance.Material3.BodyMedium">
        <item name="fontFamily">sans-serif</item>
        <item name="android:fontFamily">sans-serif</item>
        <item name="android:textSize">14sp</item>
        <item name="android:textColor">@color/text_secondary</item>
    </style>

    <style name="TextAppearance.Deeply.Caption" parent="TextAppearance.Material3.BodySmall">
        <item name="fontFamily">sans-serif</item>
        <item name="android:fontFamily">sans-serif</item>
        <item name="android:textSize">12sp</item>
        <item name="android:textColor">@color/text_tertiary</item>
    </style>

    <style name="TextAppearance.Deeply.Button" parent="TextAppearance.Material3.LabelLarge">
        <item name="fontFamily">sans-serif-medium</item>
        <item name="android:fontFamily">sans-serif-medium</item>
        <item name="android:textSize">16sp</item>
        <item name="android:textAllCaps">false</item>
    </style>

    <style name="Widget.Deeply.Button.Primary" parent="Widget.Material3.Button">
        <item name="android:minHeight">@dimen/height_button</item>
        <item name="backgroundTint">@color/accent_orange</item>
        <item name="android:textAppearance">@style/TextAppearance.Deeply.Button</item>
        <item name="android:textColor">@color/white</item>
        <item name="cornerRadius">@dimen/radius_md</item>
    </style>

    <style name="Widget.Deeply.Button.Secondary" parent="Widget.Material3.Button.OutlinedButton">
        <item name="android:minHeight">@dimen/height_button</item>
        <item name="android:textAppearance">@style/TextAppearance.Deeply.Button</item>
        <item name="cornerRadius">@dimen/radius_md</item>
        <item name="strokeColor">@color/grey_200</item>
    </style>

    <style name="Widget.Deeply.Button.PillTab" parent="Widget.Material3.Button.OutlinedButton">
        <item name="android:minHeight">0dp</item>
        <item name="android:minWidth">0dp</item>
        <item name="android:paddingTop">@dimen/space_sm</item>
        <item name="android:paddingBottom">@dimen/space_sm</item>
        <item name="android:paddingStart">@dimen/space_lg</item>
        <item name="android:paddingEnd">@dimen/space_lg</item>
        <item name="android:textAppearance">@style/TextAppearance.Deeply.BodySmall</item>
        <item name="android:textAllCaps">false</item>
        <item name="cornerRadius">@dimen/radius_pill</item>
        <item name="strokeWidth">0dp</item>
        <item name="backgroundTint">@android:color/transparent</item>
        <item name="android:textColor">@color/text_secondary</item>
        <item name="rippleColor">@color/grey_100</item>
    </style>

    <style name="Widget.Deeply.TextInputLayout" parent="Widget.Material3.TextInputLayout.OutlinedBox">
        <item name="boxCornerRadiusTopStart">@dimen/radius_md</item>
        <item name="boxCornerRadiusTopEnd">@dimen/radius_md</item>
        <item name="boxCornerRadiusBottomStart">@dimen/radius_md</item>
        <item name="boxCornerRadiusBottomEnd">@dimen/radius_md</item>
        <item name="boxStrokeColor">@color/grey_200</item>
        <item name="hintTextColor">@color/text_tertiary</item>
    </style>

</resources>
```

#### themes.xml

```xml
<resources xmlns:tools="http://schemas.android.com/tools">

    <style name="Theme.Deeply" parent="Theme.Material3.Light.NoActionBar">
        <item name="colorPrimary">@color/accent_orange</item>
        <item name="colorOnPrimary">@color/white</item>
        <item name="colorPrimaryContainer">@color/accent_orange</item>
        <item name="colorOnPrimaryContainer">@color/white</item>

        <item name="colorSurface">@color/surface</item>
        <item name="colorSurfaceVariant">@color/surface_elevated</item>

        <item name="android:windowBackground">@color/background_warm</item>
        <item name="android:colorBackground">@color/background_warm</item>

        <item name="android:textColorPrimary">@color/text_primary</item>
        <item name="android:textColorSecondary">@color/text_secondary</item>

        <item name="textAppearanceBodyLarge">@style/TextAppearance.Deeply.Body</item>
        <item name="textAppearanceBodyMedium">@style/TextAppearance.Deeply.BodySmall</item>
        <item name="textAppearanceBodySmall">@style/TextAppearance.Deeply.Caption</item>
        <item name="textAppearanceLabelLarge">@style/TextAppearance.Deeply.Button</item>
    </style>

</resources>
```

#### strings.xml

```xml
<resources>
    <string name="app_name">Deeply</string>

    <string name="presession_env_title">Where are you?</string>
    <string name="presession_env_subtitle">Choose your environment</string>
    <string name="env_home">Home</string>
    <string name="env_cafe">Cafe</string>
    <string name="env_office">Office</string>
    <string name="env_library">Library</string>
    <string name="env_other_quiet">Other (Quiet)</string>
    <string name="env_other_noisy">Other (Noisy)</string>

    <string name="presession_activity_title">What will you do?</string>
    <string name="presession_activity_subtitle">Pick your activity</string>
    <string name="activity_reading">Reading</string>
    <string name="activity_writing">Writing</string>
    <string name="activity_coding">Coding</string>
    <string name="activity_studying">Studying</string>
    <string name="activity_designing">Designing</string>
    <string name="activity_other">Other</string>

    <string name="presession_energy_title">Energy level?</string>
    <string name="presession_energy_subtitle">How are you feeling right now?</string>
    <string name="energy_low">Low</string>
    <string name="energy_medium">Medium</string>
    <string name="energy_high">High</string>

    <string name="active_session_title">Focus Session</string>
    <string name="active_session_initial_time">00:00</string>
    <string name="active_session_end">End Session</string>

    <string name="post_session_title">Session Complete</string>
    <string name="post_session_focus_quality">Focus Quality</string>
    <string name="post_session_distraction">Distraction Level</string>
    <string name="post_session_satisfaction">Satisfaction</string>
    <string name="post_session_save">Save Reflection</string>
    <string name="post_session_summary_format">%1$s · %2$s · %3$s</string>

    <string name="dashboard_greeting">Welcome back</string>
    <string name="dashboard_subtitle">Ready to focus?</string>
    <string name="dashboard_start_title">Start a session</string>
    <string name="dashboard_start_body">Set your environment, activity, and energy level.</string>
    <string name="dashboard_start_button">Begin</string>
    <string name="dashboard_history_body">View your past focus sessions</string>

    <string name="login_sign_in">Sign in</string>
    <string name="login_sign_up">Sign up</string>
    <string name="login_email_hint">Email</string>
    <string name="login_password_hint">Password</string>
    <string name="login_continue">Continue</string>
    <string name="login_divider_or">or</string>
    <string name="login_google">Continue with Google</string>

    <string name="profile_title">Profile</string>
    <string name="profile_section_account">Account</string>
    <string name="profile_edit_profile">Edit profile</string>
    <string name="profile_change_password">Change password</string>
    <string name="profile_sign_out">Sign out</string>
    <string name="profile_section_preferences">Preferences</string>
    <string name="profile_notifications">Notifications</string>
    <string name="profile_haptic_feedback">Haptic feedback</string>
    <string name="profile_theme">Theme</string>
    <string name="profile_theme_system">System</string>
    <string name="profile_section_about">About</string>
    <string name="profile_privacy_policy">Privacy policy</string>
    <string name="profile_terms_of_service">Terms of service</string>
    <string name="profile_version">Version</string>
    <string name="profile_version_number">1.0.0</string>

    <string name="history_title">History</string>
    <string name="history_empty_title">No sessions yet</string>
    <string name="history_empty_body">Complete your first focus session to see it here.</string>
    <string name="history_focus">Focus</string>
    <string name="history_distraction">Distraction</string>
    <string name="history_satisfaction">Satisfaction</string>
    <string name="history_energy">Energy</string>
    <string name="history_started">Started</string>
    <string name="history_ended">Ended</string>
</resources>
```

---

### 1.12 Build Configuration

#### app/build.gradle.kts

```kotlin
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "edu.cit.deeply"
    compileSdk = 36

    defaultConfig {
        applicationId = "edu.cit.deeply"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
```

#### gradle/libs.versions.toml

```toml
[versions]
agp = "8.3.2"
kotlin = "1.9.0"
coreKtx = "1.9.0"
junit = "4.13.2"
junitVersion = "1.3.0"
espressoCore = "3.7.0"
appcompat = "1.7.1"
material = "1.13.0"
activity = "1.8.0"
constraintlayout = "2.2.1"
coroutines = "1.7.3"

[libraries]
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
androidx-appcompat = { group = "androidx.appcompat", name = "appcompat", version.ref = "appcompat" }
material = { group = "com.google.android.material", name = "material", version.ref = "material" }
androidx-activity = { group = "androidx.activity", name = "activity", version.ref = "activity" }
androidx-constraintlayout = { group = "androidx.constraintlayout", name = "constraintlayout", version.ref = "constraintlayout" }

[plugins]
androidApplication = { id = "com.android.application", version.ref = "agp" }
jetbrainsKotlinAndroid = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
```

---

## Section 2: Vertical Slicing Architecture

Vertical Slicing Architecture is a software design approach where the codebase is organized by **feature** rather than by technical layer. Instead of grouping all Activities together, all Presenters together, and all Models together (horizontal layering), vertical slicing groups everything that belongs to a single feature into one package. In the Deeply project, this means the `screens/login/` package contains `LoginActivity`, `LoginContract`, and `LoginPresenter` side by side, rather than scattering them across separate `activities/`, `contracts/`, and `presenters/` folders.

The main advantage of vertical slicing is that it reflects how developers actually work on features. When building or modifying the Login screen, all related files are in one place. There is no need to jump between distant directories to understand how a feature works. Each vertical slice is self-contained: it defines its own View interface, its own Presenter logic, and its own Activity implementation. This makes the feature easy to understand, modify, or even delete without affecting unrelated parts of the app.

Vertical slicing also supports better team collaboration. In a team setting, different developers can work on separate feature packages (e.g., one person works on `history/` while another works on `profile/`) with minimal merge conflicts, since their changes are isolated to different directories. It also scales naturally as the app grows. Adding a new feature means adding a new package rather than modifying files spread across multiple layer folders.

---

## Section 3: Reflection

### Challenges Encountered

Building the Deeply app presented several practical challenges. The most disruptive was a runtime crash caused by downloadable Google Fonts. The font certificate XML contained base64 data that Android's resource parser could not decode on the emulator, producing a `bad base-64` error during layout inflation. The fix was to replace downloadable fonts with built-in system fonts (`sans-serif` and `serif`), which eliminated the dependency on Google Play Services entirely. Another challenge was getting the navigation flow right. Initially, the post-session screen simply called `finish()`, which closed the app because no Dashboard existed in the back stack. This required creating a proper `DashboardActivity` as a central hub and using `FLAG_ACTIVITY_CLEAR_TOP` intent flags to navigate back correctly after a session ends.

### Vertical Slicing vs. Horizontal Layers

The traditional horizontal approach groups files by type: all Activities in one folder, all Presenters in another, all Models in a third. This becomes unwieldy as the project grows because a single feature's logic is scattered across multiple directories. Vertical slicing, as used in Deeply, groups by feature instead. The `screens/presession/` package contains the Activity, Contract, and Presenter for the pre-session flow all in one place. This makes each feature self-contained and easier to reason about. The `data/` package remains shared because models and repositories are genuinely cross-cutting concerns, but UI-specific logic stays within its feature boundary.

### MVP Benefits
se
The Model-View-Presenter pattern provides a clean separation between what the user sees (View) and what the app does (Presenter). Each Activity implements a Contract View interface and delegates user actions to a Presenter. The Presenter contains the business logic and calls back to the View through the interface. This makes the code testable because the Presenter can be unit-tested without an Android device by mocking the View interface. It also prevents Activities from becoming massive classes that handle both UI rendering and business logic, a common problem known as "God Activity."

### Reflection on the Process

Working through the full session loop (PreSession, ActiveSession, PostSession) as a connected vertical slice, rather than building all models first, then all presenters, then all views, made it possible to see each feature working end-to-end quickly. Each slice could be developed, tested, and refined independently before moving to the next one. This iterative approach caught integration issues early, such as the missing Dashboard navigation, instead of discovering them only after all layers were assembled.
