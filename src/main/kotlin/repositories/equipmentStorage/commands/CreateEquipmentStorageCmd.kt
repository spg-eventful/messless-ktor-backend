package at.eventful.messless.repositories.equipmentStorage.commands

import kotlinx.serialization.Serializable

@Serializable
data class CreateEquipmentStorageCmd(
    val label: String,
    val latitude: Double,
    val longitude: Double,
)