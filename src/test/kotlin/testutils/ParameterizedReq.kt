import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.schema.dao.UserDao

data class ParameterizedReq(
    val name: String,
    val user: UserDao,
    val expectedStatus: Int,
    val method: Method,
    val payload: String?
) {
    override fun toString(): String =
        "${method.name} ${user.role.name} $name"
}