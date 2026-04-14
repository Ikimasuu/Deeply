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
