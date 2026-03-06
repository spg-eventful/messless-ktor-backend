package at.eventful.messless.services.warehouse

import kotlinx.serialization.Serializable

@Serializable
data class ExposedWarehouse(
    val id: Int,
    val label: String,
    val locationX: Double,
    val locationY: Double,
    val companyName: String
)

@Serializable
data class Warehouse(
    val id: Int,
    val label: String,
    val locationX: Double,
    val locationY: Double,
    val companyName: String
) {
    fun toExposedWarehouse() = ExposedWarehouse (
        id,
        label,
        locationX,
        locationY,
        companyName
    )
}

@Serializable
data class CreateWarehouseCommand(
    val label: String,
    val locationX: Double,
    val locationY: Double,
)