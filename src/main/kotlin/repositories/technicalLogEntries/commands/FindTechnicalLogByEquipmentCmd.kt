package at.eventful.messless.repositories.technicalLogEntries.commands

import kotlinx.serialization.Serializable

@Serializable
data class FindTechnicalLogByEquipmentCmd(val equipmentId: Int)