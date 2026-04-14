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
