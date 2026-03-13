package at.eventful.messless.schema.dto

import at.eventful.messless.schema.dao.TechnicalLogEntryDao
import kotlinx.serialization.Serializable

//TODO: rework attachedTo
@Serializable
data class TechnicalLogEntryDto(
    val id: Int,
    val isCheckIn: Boolean,
    val attachedTo: Int,
    val byUser: Int?,
    val userFullName: String,
    val loggable: Int
) {
    companion object{
        fun from(technicalLogEntry: TechnicalLogEntryDao) = TechnicalLogEntryDto(
            id = technicalLogEntry.id,
            isCheckIn = technicalLogEntry.isCheckIn,
            attachedTo = technicalLogEntry.attachedTo,
            byUser = technicalLogEntry.byUser?.id,
            userFullName = "${technicalLogEntry.byUser?.firstName} ${technicalLogEntry.byUser?.lastName}",
            loggable = technicalLogEntry.loggable,
        )
    }
}