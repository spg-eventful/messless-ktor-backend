package services.users

import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.repositories.users.commands.UpdateUserCmd
import at.eventful.messless.schema.dao.UserDao
import at.eventful.messless.schema.utils.UserRole
import io.ktor.client.plugins.websocket.*
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import repositories.users.UserRepository
import repositories.users.commands.CreateUserCmd
import testutils.configuredTestApplication
import testutils.sendAndAssert
import testutils.sendLoginFrame


@ExtendWith(MockKExtension::class)
class UsersServiceTest {
    val usersRepository = mockk<UserRepository>()

    companion object {
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

        val admin = UserDao.fake(1).copy(role = UserRole.Admin)
        val owner = UserDao.fake(2).copy(role = UserRole.CompanyAdmin)
        val stranger = UserDao.fake(3).copy(role = UserRole.Worker)

        val updateCmd = UpdateUserCmd(
            owner.id,
            owner.email,
            owner.phone,
            owner.firstName,
            owner.lastName,
            owner.role,
        )
        val createCmd = CreateUserCmd(
            owner.email,
            "banane",
            owner.role,
            owner.phone,
            owner.firstName,
            owner.lastName,
        )

        @JvmStatic
        fun requestMatrix() = listOf(
            // CREATE
            ParameterizedReq("creates owner", owner, 201, Method.CREATE, Json.encodeToString(createCmd)),
            // TODO: Test who can create what role
            // READ
            ParameterizedReq("reads owner", admin, 200, Method.READ, owner.id.toString()),
            ParameterizedReq("reads owner", owner, 200, Method.READ, owner.id.toString()),
            ParameterizedReq("reads owner", stranger, 403, Method.READ, owner.id.toString()),
            // READ ALL
            ParameterizedReq("reads all", admin, 200, Method.READ, null),
            ParameterizedReq("reads all", owner, 200, Method.READ, null),
            ParameterizedReq("reads all", stranger, 200, Method.READ, null),
            // UPDATE
            ParameterizedReq("update owner", admin, 200, Method.UPDATE, Json.encodeToString(updateCmd)),
            ParameterizedReq("update owner", owner, 200, Method.UPDATE, Json.encodeToString(updateCmd)),
            ParameterizedReq("update owner", stranger, 403, Method.UPDATE, Json.encodeToString(updateCmd)),
            // DELETE
            ParameterizedReq("delete owner", admin, 204, Method.DELETE, owner.id.toString()),
            ParameterizedReq("delete owner", owner, 204, Method.DELETE, owner.id.toString()),
            ParameterizedReq("delete owner", stranger, 403, Method.DELETE, owner.id.toString()),
        )
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("requestMatrix")
    fun makeRequest(
        pr: ParameterizedReq
    ) = configuredTestApplication {
        dependencies.provide<UserRepository> { usersRepository }
        every { usersRepository.addUser(createCmd) } returns owner
        every { usersRepository.allUsers() } returns listOf(admin, owner, stranger)
        every { usersRepository.userById(admin.id) } returns admin
        every { usersRepository.userById(owner.id) } returns owner
        every { usersRepository.userById(stranger.id) } returns stranger
        every { usersRepository.updateUser(owner.id, updateCmd) } returns owner
        every { usersRepository.removeUser(owner.id) } returns owner

        client.webSocket("/ws") {
            run {
                sendLoginFrame(this@configuredTestApplication, pr.user)
                sendAndAssert("users", pr.method, pr.payload, pr.expectedStatus)
            }
        }
    }
}