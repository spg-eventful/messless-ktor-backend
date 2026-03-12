package repositories.warehouse.command

import kotlinx.serialization.Serializable

@Serializable
data class CreateWarehouseCmd(
    val label: String,
    val latitude: Double,
    val longitude: Double,
)