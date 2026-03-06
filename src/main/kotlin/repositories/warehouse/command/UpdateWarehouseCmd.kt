package repositories.warehouse.command

import kotlinx.serialization.Serializable

@Serializable
data class UpdateWarehouseCmd(
    val id: Int,
    val label: String,
    val locationX: Double,
    val locationY: Double
)