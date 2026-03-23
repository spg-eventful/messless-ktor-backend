package at.eventful.messless.schema.dto

import at.eventful.messless.schema.dao.TechnicalLogEntryDao
import kotlinx.serialization.Serializable

@Serializable
data class TechnicalLogEntryDto(
    val id: Int,
    val isCheckIn: Boolean,
    val attachedTo: Int,
    val equipmentLabel: String,
    val byUser: Int?,
    val userFullName: String,
    val loggable: Int,
    val status: String,
) {
    companion object{
        fun from(technicalLogEntry: TechnicalLogEntryDao) = TechnicalLogEntryDto(
            id = technicalLogEntry.id,
            isCheckIn = technicalLogEntry.isCheckIn,
            attachedTo = technicalLogEntry.attachedTo?.id ?: throw Exception("Equipment not found"),
            equipmentLabel = technicalLogEntry.attachedTo.label,
            byUser = technicalLogEntry.byUser?.id,
            userFullName = "${technicalLogEntry.byUser?.firstName} ${technicalLogEntry.byUser?.lastName}",
            loggable = technicalLogEntry.loggable,
            status = technicalLogEntry.status.toString(),
            )
    }
}