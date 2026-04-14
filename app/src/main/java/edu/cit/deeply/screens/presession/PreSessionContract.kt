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
