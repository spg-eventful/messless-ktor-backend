package at.eventful.messless.repositories.company

import at.eventful.messless.repositories.company.commands.CreateCompanyCmd
import at.eventful.messless.repositories.company.commands.UpdateCompanyCmd
import at.eventful.messless.schema.dao.CompanyDao

interface CompanyRepository {
    fun addCompany(company: CreateCompanyCmd): CompanyDao
    fun allCompanies(): List<CompanyDao>
    fun companyById(id: Int): CompanyDao?
    fun updateCompany(id: Int, company: UpdateCompanyCmd): CompanyDao?
    fun removeCompany(id: Int): CompanyDao?
}