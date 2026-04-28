package at.eventful.messless.repositories.warehouse.command

import kotlinx.serialization.Serializable

@Serializable
data class UpdateWarehouseCmd(
    val `$id`: Int,
    val label: String,
    val latitude: Double,
    val longitude: Double,
    val companyId: Int,
)