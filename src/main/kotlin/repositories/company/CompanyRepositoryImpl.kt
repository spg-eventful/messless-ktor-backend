package at.eventful.messless.repositories.company

import at.eventful.messless.repositories.company.commands.CreateCompanyCmd
import at.eventful.messless.repositories.company.commands.UpdateCompanyCmd
import at.eventful.messless.schema.dao.CompanyDao
import at.eventful.messless.schema.entities.CompanyEntity
import at.eventful.messless.schema.tables.CompanyTable
import net.postgis.jdbc.geometry.Point
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CompanyRepositoryImpl : CompanyRepository {
    override fun addCompany(company: CreateCompanyCmd): CompanyDao = transaction {
        CompanyDao.from(CompanyEntity.new {
            label = company.label
            location = Point(company.longitude, company.latitude)
        })!!
    }

    @OptIn(ExperimentalTime::class)
    override fun allCompanies(): List<CompanyDao> = transaction {
        val mapper: (CompanyEntity?) -> CompanyDao? = CompanyDao::from
        CompanyEntity.find { CompanyTable.deletedAt eq null }.toList()
            .map { mapper } as List<CompanyDao>
    }

    @OptIn(ExperimentalTime::class)
    override fun companyById(id: Int): CompanyDao? = transaction {
        val company = CompanyEntity.findById(id)
        return@transaction if (company?.deletedAt == null) CompanyDao.from(company) else null
    }

    override fun updateCompany(id: Int, company: UpdateCompanyCmd): CompanyDao? = transaction {
        CompanyDao.from(CompanyEntity.findByIdAndUpdate(id) {
            it.label = company.label
            it.location = Point(company.longitude, company.latitude)
        })
    }

    @OptIn(ExperimentalTime::class)
    override fun removeCompany(id: Int): CompanyDao? = transaction {
        CompanyDao.from(CompanyEntity.findByIdAndUpdate(id) {
            it.deletedAt = Clock.System.now()
        })
    }
}