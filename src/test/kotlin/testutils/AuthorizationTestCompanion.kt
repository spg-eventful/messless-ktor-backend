package testutils

import at.eventful.messless.schema.dao.CompanyDao
import at.eventful.messless.schema.dao.UserDao
import at.eventful.messless.schema.utils.UserRole

open class AuthorizationTestCompanion {
    object CompanyOne {
        val company = CompanyDao.fake(1)
        val admin = UserDao.fake(1).copy(role = UserRole.Admin, company = company)
        val owner = UserDao.fake(2).copy(role = UserRole.CompanyAdmin, company = company)
        val worker = UserDao.fake(3).copy(role = UserRole.Worker, company = company)
    }

    object CompanyTwo {
        val company = CompanyDao.fake(2)
        val admin = UserDao.fake(4).copy(role = UserRole.Admin, company = company)
        val owner = UserDao.fake(5).copy(role = UserRole.CompanyAdmin, company = company)
        val worker = UserDao.fake(6).copy(role = UserRole.Worker, company = company)
    }
}