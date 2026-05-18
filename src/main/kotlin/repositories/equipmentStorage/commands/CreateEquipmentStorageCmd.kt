package at.eventful.messless.repositories.equipmentStorage.commands

import kotlinx.serialization.Serializable

@Serializable
data class CreateEquipmentStorageCmd(
    val label: String,
    val longitude: Double,
    val latitude: Double,
    val companyId: Int,
)