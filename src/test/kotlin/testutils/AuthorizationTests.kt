package testutils

import at.eventful.messless.schema.dao.CompanyDao
import at.eventful.messless.schema.dao.UserDao
import at.eventful.messless.schema.utils.UserRole

open class AuthorizationTest {
    val admin = UserDao.fake(1).copy(role = UserRole.Admin, company = CompanyDao.fake(1))
    val owner = UserDao.fake(2).copy(role = UserRole.CompanyAdmin, company = CompanyDao.fake(1))
    val worker = UserDao.fake(3).copy(role = UserRole.Worker, company = CompanyDao.fake(1))

    val strangerOwner = UserDao.fake(5).copy(role = UserRole.CompanyAdmin, company = CompanyDao.fake(2))
    val stranger = UserDao.fake(4).copy(role = UserRole.Worker, company = CompanyDao.fake(2))
}