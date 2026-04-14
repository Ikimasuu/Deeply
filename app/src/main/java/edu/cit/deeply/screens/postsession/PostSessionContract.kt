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
