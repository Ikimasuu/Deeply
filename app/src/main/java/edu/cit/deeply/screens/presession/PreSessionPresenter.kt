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
