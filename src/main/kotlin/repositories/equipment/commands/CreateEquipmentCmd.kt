package at.eventful.messless.repositories.equipment.commands

import kotlinx.serialization.Serializable

@Serializable
data class CreateEquipmentCmd(
    var label: String,
    var belongsToWarehouse: Int,
    var isStorage: Boolean = false,
)