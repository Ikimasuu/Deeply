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
