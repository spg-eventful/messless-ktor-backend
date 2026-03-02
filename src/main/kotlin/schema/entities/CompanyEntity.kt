package at.eventful.messless.schema.entities

import at.eventful.messless.schema.tables.CompanyTable
import at.eventful.messless.schema.utils.BaseEntity
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntityClass
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class CompanyEntity(id: EntityID<Int>) : BaseEntity(id) {

    companion object : IntEntityClass<CompanyEntity>(CompanyTable)

    override var createdAt by CompanyTable.createdAt
    override var updatedAt by CompanyTable.updatedAt
    override var deletedAt by CompanyTable.deletedAt

    var label by CompanyTable.label
    var location by CompanyTable.location

}