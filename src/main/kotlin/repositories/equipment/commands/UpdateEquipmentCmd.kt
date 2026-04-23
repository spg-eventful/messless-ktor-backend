package at.eventful.messless.repositories.equipment.commands

import kotlinx.serialization.Serializable

@Serializable
data class UpdateEquipmentCmd(
    val `$id`: Int,
    var label: String,
    var belongsToWarehouse: Int,
    var equipmentStorage: Int?,
    var longitude: Double?,
    var latitude: Double?,
)