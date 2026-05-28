package at.eventful.messless.repositories.warehouse.command

import kotlinx.serialization.Serializable

@Serializable
data class CreateWarehouseCmd(
    val label: String,
    val longitude: Double,
    val latitude: Double,
    val companyId: Int,
)