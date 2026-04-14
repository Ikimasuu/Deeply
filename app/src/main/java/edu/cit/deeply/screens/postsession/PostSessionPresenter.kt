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
