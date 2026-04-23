package services.users

import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.repositories.users.UserRepository
import at.eventful.messless.repositories.users.commands.CreateUserCmd
import at.eventful.messless.repositories.users.commands.UpdateUserCmd
import at.eventful.messless.schema.utils.UserRole
import io.ktor.client.plugins.websocket.*
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import testutils.*
import testutils.AuthorizationTestCompanion.CompanyOne


@ExtendWith(MockKExtension::class)
class UsersServiceTest : AuthorizationTest() {
    override val usersRepository = mockk<UserRepository>()

    companion object : AuthorizationTestCompanion() {
        val updateCmd = UpdateUserCmd(
            CompanyOne.owner.id,
            CompanyOne.owner.email,
            CompanyOne.owner.phone,
            CompanyOne.owner.firstName,
            CompanyOne.owner.lastName,
            CompanyOne.owner.role,
            CompanyOne.owner.company?.id ?: 1,
        )
        val createCmd = CreateUserCmd(
            CompanyOne.owner.email,
            "banane",
            CompanyOne.owner.role,
            CompanyOne.owner.phone,
            CompanyOne.owner.firstName,
            CompanyOne.owner.lastName,
            CompanyOne.owner.company?.id ?: 1,
        )

        @JvmStatic
        fun requestMatrix() = listOf(
            // CREATE
            ParameterizedReq(
                "creates admin",
                CompanyOne.admin,
                201,
                Method.CREATE,
                Json.encodeToString(createCmd.copy(role = UserRole.Admin))
            ),
            ParameterizedReq("creates owner", CompanyOne.owner, 201, Method.CREATE, Json.encodeToString(createCmd)),
            ParameterizedReq(
                "creates owner role above allowed",
                CompanyOne.owner,
                403,
                Method.CREATE,
                Json.encodeToString(createCmd.copy(role = UserRole.Admin))
            ),
            // READ
            ParameterizedReq("reads owner", CompanyOne.admin, 200, Method.READ, CompanyOne.owner.id.toString()),
            ParameterizedReq("reads owner", CompanyOne.owner, 200, Method.READ, CompanyOne.owner.id.toString()),
            ParameterizedReq("reads owner", CompanyOne.worker, 403, Method.READ, CompanyOne.owner.id.toString()),
            // READ ALL
            ParameterizedReq("reads all", CompanyOne.admin, 200, Method.READ, null),
            ParameterizedReq("reads all", CompanyOne.owner, 200, Method.READ, null),
            ParameterizedReq("reads all", CompanyOne.worker, 200, Method.READ, null),
            // UPDATE
            ParameterizedReq("update owner", CompanyOne.admin, 200, Method.UPDATE, Json.encodeToString(updateCmd)),
            ParameterizedReq("update owner", CompanyOne.owner, 200, Method.UPDATE, Json.encodeToString(updateCmd)),
            ParameterizedReq(
                "update owner role above allowed",
                CompanyOne.owner,
                403,
                Method.UPDATE,
                Json.encodeToString(updateCmd.copy(role = UserRole.Admin))
            ),
            ParameterizedReq("update owner", CompanyOne.worker, 403, Method.UPDATE, Json.encodeToString(updateCmd)),
            // DELETE
            ParameterizedReq("delete owner", CompanyOne.admin, 204, Method.DELETE, CompanyOne.owner.id.toString()),
            ParameterizedReq("delete owner", CompanyOne.owner, 204, Method.DELETE, CompanyOne.owner.id.toString()),
            ParameterizedReq("delete owner", CompanyOne.worker, 403, Method.DELETE, CompanyOne.owner.id.toString()),
        )
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("requestMatrix")
    override fun makeRequest(
        pr: ParameterizedReq
    ) = configuredTestApplication {
        dependencies.provide<UserRepository> { usersRepository }
        every { usersRepository.addUser(any()) } returns CompanyOne.owner
        every { usersRepository.allUsers() } returns listOf(CompanyOne.admin, CompanyOne.owner, CompanyOne.worker)
        every { usersRepository.updateUser(CompanyOne.owner.id, updateCmd) } returns CompanyOne.owner
        every { usersRepository.removeUser(CompanyOne.owner.id) } returns CompanyOne.owner
        mockAuthRelatedMethods()

        client.webSocket("/ws") {
            run {
                sendLoginFrame(this@configuredTestApplication, pr.user)
                sendAndAssert("users", pr.method, pr.payload, pr.expectedStatus)
            }
        }
    }
}