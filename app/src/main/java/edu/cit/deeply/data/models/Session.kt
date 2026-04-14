package edu.cit.deeply.data.models

data class Session(
    val id: String,
    val environment: Environment,
    val activity: Activity,
    val energy: EnergyLevel,
    val startTime: Long,
    var endTime: Long? = null,
    var focusQuality: Int? = null,
    var distractionLevel: Int? = null,
    var satisfaction: Int? = null
)
