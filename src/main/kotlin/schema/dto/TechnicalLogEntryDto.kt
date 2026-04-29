package at.eventful.messless.schema.dto

import at.eventful.messless.schema.dao.LoggableDao
import at.eventful.messless.schema.dao.TechnicalLogEntryDao
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@Serializable
data class TechnicalLogEntryDto(
    val id: Int,
    val isCheckIn: Boolean,
    val attachedTo: Int,
    val equipmentLabel: String,
    val byUser: Int?,
    val userFullName: String,
    val longitude: Double?,
    val latitude: Double?,
    val createdAt: String = "",
) {
    companion object{
        @OptIn(ExperimentalTime::class)
        fun from(technicalLogEntry: TechnicalLogEntryDao, loggable: LoggableDao) = TechnicalLogEntryDto(
            id = technicalLogEntry.id,
            isCheckIn = technicalLogEntry.isCheckIn,
            attachedTo = technicalLogEntry.attachedTo?.id ?: throw Exception("Equipment not found"),
            equipmentLabel = technicalLogEntry.attachedTo.label,
            byUser = technicalLogEntry.byUser?.id,
            userFullName = "${technicalLogEntry.byUser?.firstName} ${technicalLogEntry.byUser?.lastName}",
            longitude = technicalLogEntry.longitude,
            latitude = technicalLogEntry.latitude,
            createdAt = technicalLogEntry.createdAt.toString(),
            )
    }
}