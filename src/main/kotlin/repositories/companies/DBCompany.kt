package at.eventful.messless.repositories.companies

import at.eventful.messless.repositories.IConvertibleDBType
import at.eventful.messless.schema.entities.CompanyEntity
import kotlinx.serialization.Serializable

// TODO
@Serializable
data class DBCompany(val id: Int) {
    companion object : IConvertibleDBType<CompanyEntity, DBCompany> {
        override fun from(entity: CompanyEntity?): DBCompany? {
            TODO("Not yet implemented")
        }
    }
}
