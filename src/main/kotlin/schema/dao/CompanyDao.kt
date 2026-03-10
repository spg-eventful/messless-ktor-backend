package at.eventful.messless.schema.dao

import at.eventful.messless.schema.entities.CompanyEntity
import kotlinx.serialization.Serializable

// TODO
@Serializable
data class CompanyDao(val id: Int) {
    companion object : ConvertibleDao<CompanyEntity, CompanyDao> {
        override fun from(entity: CompanyEntity?): CompanyDao? {
            TODO("Not yet implemented")
        }
    }
}