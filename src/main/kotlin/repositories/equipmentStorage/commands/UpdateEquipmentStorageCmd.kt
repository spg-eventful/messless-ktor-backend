package at.eventful.messless.repositories.equipmentStorage.commands

import kotlinx.serialization.Serializable

@Serializable
data class UpdateEquipmentStorageCmd(
    val `$id`: Int,
    val label: String,
    val latitude: Double,
    val longitude: Double,
)