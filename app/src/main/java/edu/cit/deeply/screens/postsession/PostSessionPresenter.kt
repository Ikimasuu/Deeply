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
        val totalMinutes = (durationMs / 1000 / 60).coerceAtLeast(1)
        val duration = "$totalMinutes min"

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

    override fun onSkipClicked() {
        val id = sessionId ?: run {
            view?.navigateToFinish()
            return
        }
        SessionRepository.updateSession(id) { session ->
            session.focusQuality = 5
            session.distractionLevel = 5
            session.satisfaction = 5
        }
        view?.navigateToFinish()
    }
}
