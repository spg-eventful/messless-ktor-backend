package at.eventful.messless.repositories.technicalLogEntries.commands

import at.eventful.messless.schema.utils.Status
import kotlinx.serialization.Serializable

@Serializable
class CreateTechnicalLogEntryCmd(
    val isCheckIn: Boolean,
    val attachedTo: Int,
    val byUser: Int,
    val loggable: Int,
    val status: Status,
)