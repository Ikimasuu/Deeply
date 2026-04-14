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
