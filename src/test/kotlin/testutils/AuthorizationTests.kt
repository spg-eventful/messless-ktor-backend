package testutils

import at.eventful.messless.schema.dao.UserDao
import at.eventful.messless.schema.utils.UserRole

open class AuthorizationTest {
    val admin = UserDao.fake(1).copy(role = UserRole.Admin)
    val owner = UserDao.fake(2).copy(role = UserRole.CompanyAdmin)
    val stranger = UserDao.fake(3).copy(role = UserRole.Worker)
}