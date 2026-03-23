package services.warehouses

import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.repositories.technicalLogEntries.TechnicalLogEntryRepository
import at.eventful.messless.repositories.technicalLogEntries.commands.FindTechnicalLogByEquipmentCmd
import io.ktor.client.plugins.websocket.*
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import repositories.users.UserRepository
import testutils.*

@ExtendWith(MockKExtension::class)
class TechnicalLogServiceTest : AuthorizationTest() {
    val technicalLogRepository = mockk<TechnicalLogEntryRepository>()
    override val usersRepository = mockk<UserRepository>()

    companion object : AuthorizationTestCompanion() {
        val equipmentId = 1

        val findCmd = FindTechnicalLogByEquipmentCmd(
            equipmentId
        )

        @JvmStatic
        fun requestMatrix() = listOf(
            // CREATE
            ParameterizedReq("find technical log entries", CompanyOne.owner, 200, Method.READ, Json.encodeToString(findCmd)),
        )
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("requestMatrix")
    override fun makeRequest(
        pr: ParameterizedReq
    ) = configuredTestApplication {
        // dependencies.provide<WarehouseRepository> { technicalLogRepository }
        dependencies.provide<UserRepository> { usersRepository }
        // every { technicalLogRepository.() } returns listOf(warehouse)
        mockAuthRelatedMethods()

        client.webSocket("/ws") {
            run {
                sendLoginFrame(this@configuredTestApplication, pr.user)
                sendAndAssert("technical-log-entries", pr.method, pr.payload, pr.expectedStatus)
            }
        }
    }
}