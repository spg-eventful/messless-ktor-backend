package at.eventful.messless.schema.dao

import at.eventful.messless.schema.entities.CompanyEntity
import kotlinx.serialization.Serializable

@Serializable
data class CompanyDao(
    val id: Int,
    val label: String,
    val longitude: Double,
    val latitude: Double,
) {
    companion object : ConvertibleDao<CompanyEntity, CompanyDao> {
        override fun from(entity: CompanyEntity?): CompanyDao? = entity?.let{
            CompanyDao(
                id = entity.id.value,
                label = entity.label,
                longitude = entity.location.x,
                latitude = entity.location.y,
            )
        }

        fun fake(id: Int): CompanyDao = CompanyDao(
            id = id,
            label = "Fake company",
            longitude = 0.0,
            latitude = 0.0,
        )
    }
}