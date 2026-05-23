package edu.cit.deeply.data

import edu.cit.deeply.data.models.Activity
import edu.cit.deeply.data.models.EnergyLevel
import edu.cit.deeply.data.models.Environment
import edu.cit.deeply.data.models.Session
import edu.cit.deeply.data.repositories.SessionRepository

object DemoDataSeeder {

    fun seed() {
        if (SessionRepository.getAllSessions().isNotEmpty()) return

        val now = System.currentTimeMillis()
        val hour = 3_600_000L
        val minute = 60_000L

        val s1 = Session(
            id = "demo-1",
            environment = Environment.HOME,
            activity = Activity.CODING,
            energy = EnergyLevel.HIGH,
            startTime = now - (2 * hour),
            endTime = now - (2 * hour) + (52 * minute),
            focusQuality = 9,
            distractionLevel = 3,
            satisfaction = 9
        )

        val s2 = Session(
            id = "demo-2",
            environment = Environment.HOME,
            activity = Activity.READING_RESEARCH,
            energy = EnergyLevel.MEDIUM,
            startTime = now - (5 * hour),
            endTime = now - (5 * hour) + (28 * minute),
            focusQuality = 7,
            distractionLevel = 4,
            satisfaction = 7
        )

        val s3 = Session(
            id = "demo-3",
            environment = Environment.CAFE,
            activity = Activity.WRITING,
            energy = EnergyLevel.MEDIUM,
            startTime = now - (24 * hour) - (3 * hour),
            endTime = now - (24 * hour) - (3 * hour) + (38 * minute),
            focusQuality = 6,
            distractionLevel = 5,
            satisfaction = 6
        )

        val s4 = Session(
            id = "demo-4",
            environment = Environment.OFFICE,
            activity = Activity.PROBLEM_SOLVING,
            energy = EnergyLevel.HIGH,
            startTime = now - (24 * hour) - (6 * hour),
            endTime = now - (24 * hour) - (6 * hour) + (65 * minute),
            focusQuality = 9,
            distractionLevel = 2,
            satisfaction = 8
        )

        listOf(s1, s2, s3, s4).forEach { SessionRepository.startSession(it) }
    }
}
