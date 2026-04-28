package services.technicalLogEntries

import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.repositories.equipment.EquipmentRepository
import at.eventful.messless.repositories.event.EventRepository
import at.eventful.messless.repositories.loggable.LoggableRepository
import at.eventful.messless.repositories.technicalLogEntries.TechnicalLogEntryRepository
import at.eventful.messless.repositories.technicalLogEntries.commands.CreateTechnicalLogEntryCmd
import at.eventful.messless.repositories.technicalLogEntries.commands.FindTechnicalLogByEquipmentCmd
import at.eventful.messless.repositories.users.UserRepository
import at.eventful.messless.repositories.warehouse.WarehouseRepository
import at.eventful.messless.schema.dao.EquipmentDao
import at.eventful.messless.schema.dao.EventDao
import at.eventful.messless.schema.dao.LoggableDao
import at.eventful.messless.schema.dao.TechnicalLogEntryDao
import at.eventful.messless.schema.dao.WarehouseDao
import io.ktor.client.plugins.websocket.*
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import testutils.*

@ExtendWith(MockKExtension::class)
class TechnicalLogServiceTest : AuthorizationTest() {
    val technicalLogRepository = mockk<TechnicalLogEntryRepository>()
    override val usersRepository = mockk<UserRepository>()
    val equipmentRepository = mockk<EquipmentRepository>()
    val warehouseRepository = mockk<WarehouseRepository>()
    val loggableRepository = mockk<LoggableRepository>()
    val eventRepository = mockk<EventRepository>()

    companion object : AuthorizationTestCompanion() {
        val technicalLog = TechnicalLogEntryDao.fake(1)

        val createCmd = CreateTechnicalLogEntryCmd(
            technicalLog.isCheckIn,
            technicalLog.attachedTo?.id ?: 1,
            technicalLog.byUser?.id ?: 1,
            technicalLog.longitude ?: 0.0,
            technicalLog.latitude ?: 0.0,
        )

        val findCmd = FindTechnicalLogByEquipmentCmd(
            technicalLog.attachedTo?.id ?: 1,
        )

        @JvmStatic
        fun requestMatrix() = listOf(
            // CREATE
            ParameterizedReq(
                "create technical log entry",
                CompanyOne.owner,
                201,
                Method.CREATE,
                Json.encodeToString(createCmd)
            ),
            ParameterizedReq(
                "create technical log entry with wrong user",
                CompanyTwo.owner,
                403,
                Method.CREATE,
                Json.encodeToString(createCmd)
            ),
            // FIND
            ParameterizedReq(
                "find technical log entries",
                CompanyOne.owner,
                200,
                Method.READ,
                Json.encodeToString(findCmd)
            ),
            ParameterizedReq(
                "find technical log entries with wrong user",
                CompanyTwo.owner,
                403,
                Method.READ,
                Json.encodeToString(findCmd)
            ),
            // DELETE
            ParameterizedReq(
                "delete technical log entry",
                CompanyOne.owner,
                204,
                Method.DELETE,
                technicalLog.id.toString()
            ),
            ParameterizedReq(
                "delete technical log entry with wrong user",
                CompanyTwo.owner,
                403,
                Method.DELETE,
                technicalLog.id.toString()
            )
        )
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("requestMatrix")
    override fun makeRequest(
        pr: ParameterizedReq
    ) = configuredTestApplication {
        dependencies.provide<UserRepository> { usersRepository }
        dependencies.provide<TechnicalLogEntryRepository> { technicalLogRepository }
        dependencies.provide<EquipmentRepository> { equipmentRepository }
        dependencies.provide<WarehouseRepository> { warehouseRepository }
        dependencies.provide<EquipmentRepository> { equipmentRepository }
        dependencies.provide<LoggableRepository> { loggableRepository }
        dependencies.provide<EventRepository> { eventRepository }

        every { technicalLogRepository.allTechnicalLogEntries() } returns listOf(technicalLog)
        every { technicalLogRepository.removeTechnicalLogEntry(any()) } returns technicalLog
        every { technicalLogRepository.technicalLogEntryById(any()) } returns technicalLog
        every { equipmentRepository.equipmentById(any()) } returns EquipmentDao.fake(1)
        every { warehouseRepository.warehouseById(any()) } returns WarehouseDao.fake(1)
        every { equipmentRepository.equipmentById(any()) } returns EquipmentDao.fake(1)
        every { equipmentRepository.allEquipment() } returns listOf(EquipmentDao.fake(1))
        every { loggableRepository.loggableById(any()) } returns LoggableDao.fake(1)
        every { eventRepository.eventById(any()) } returns EventDao.fake(1)

        mockAuthRelatedMethods()

        client.webSocket("/ws") {
            run {
                sendLoginFrame(this@configuredTestApplication, pr.user)
                sendAndAssert("technical-log-entries", pr.method, pr.payload, pr.expectedStatus)
            }
        }
    }
}