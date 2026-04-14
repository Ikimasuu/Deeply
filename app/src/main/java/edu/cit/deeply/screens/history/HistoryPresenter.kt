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
