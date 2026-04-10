package at.eventful.messless.repositories.company.commands

import kotlinx.serialization.Serializable

@Serializable
data class UpdateCompanyCmd(
    val `$id`: Int,
    var label: String,
    var longitude: Double,
    var latitude: Double,
)