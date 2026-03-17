package testutils

import at.eventful.messless.schema.dao.CompanyDao
import at.eventful.messless.schema.dao.UserDao
import at.eventful.messless.schema.utils.UserRole

open class AuthorizationTest {
    val company = CompanyDao.fake(1);
    val admin = UserDao.fake(1).copy(role = UserRole.Admin, company = company)
    val owner = UserDao.fake(2).copy(role = UserRole.CompanyAdmin, company = company)
    val stranger = UserDao.fake(3).copy(role = UserRole.Worker, company = company)
}