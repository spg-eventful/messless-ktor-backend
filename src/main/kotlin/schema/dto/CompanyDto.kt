package at.eventful.messless.schema.dto

import at.eventful.messless.schema.dao.CompanyDao
import kotlinx.serialization.Serializable

@Serializable
data class CompanyDto(
    val id: Int,
    val label: String,
    val longitude: Double,
    val latitude: Double,
) {
    companion object {
        fun from(company: CompanyDao): CompanyDto = CompanyDto(
            id = company.id,
            label = company.label,
            longitude = company.longitude,
            latitude = company.latitude,
        )
    }
}