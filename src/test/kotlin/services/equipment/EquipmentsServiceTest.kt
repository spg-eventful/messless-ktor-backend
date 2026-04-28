package services.equipment

import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.repositories.company.CompanyRepository
import at.eventful.messless.repositories.equipment.EquipmentRepository
import at.eventful.messless.repositories.equipment.commands.CreateEquipmentCmd
import at.eventful.messless.repositories.equipment.commands.UpdateEquipmentCmd
import at.eventful.messless.repositories.equipmentStorage.EquipmentStorageRepository
import at.eventful.messless.repositories.equipmentStorage.commands.UpdateEquipmentStorageCmd
import at.eventful.messless.repositories.loggable.LoggableRepository
import at.eventful.messless.repositories.loggable.command.UpdateLoggableCmd
import at.eventful.messless.repositories.users.UserRepository
import at.eventful.messless.repositories.warehouse.WarehouseRepository
import at.eventful.messless.schema.dao.*
import at.eventful.messless.schema.utils.LoggableType
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

@ExtendWith(MockKExtension::class)
class EquipmentsServiceTest : AuthorizationTest() {
    val equipmentRepository = mockk<EquipmentRepository>()
    override val usersRepository = mockk<UserRepository>()
    val warehouseRepository = mockk<WarehouseRepository>()
    val equipmentStorageRepository = mockk<EquipmentStorageRepository>()
    val loggableRepository = mockk<LoggableRepository>()
    val companyRepository = mockk<CompanyRepository>()

    companion object : AuthorizationTestCompanion() {
        val equipment = EquipmentDao.fake(1)
        val warehouse = WarehouseDao.fake(1)
        val equipmentStorage = EquipmentStorageDao.fake(1)
        val loggable = LoggableDao.fake(1)
        val company = CompanyDao.fake(1)

        val updateCmd = UpdateEquipmentCmd(
            equipment.id,
            equipment.label,
            equipment.belongsToWarehouse,
            equipment.storage,
            loggable.longitude,
            loggable.latitude,
        )

        val createCmd = CreateEquipmentCmd(
            equipment.label,
            equipment.belongsToWarehouse,
            equipment.isStorage,
        )

        val updateStorageCmd = UpdateEquipmentStorageCmd(
            equipmentStorage.id,
            equipmentStorage.loggable?.label ?: throw IllegalStateException("Equipment storage has no loggable!"),
            equipmentStorage.loggable.latitude,
            equipmentStorage.loggable.longitude,
            company.id
        )

        val updateLoggableCmd = UpdateLoggableCmd(
            loggable.id,
            equipment.label,
            loggable.longitude,
            loggable.latitude,
            LoggableType.Equipment,
            company.id
        )

        @JvmStatic
        fun requestMatrix() = listOf(
            // CREATE
            ParameterizedReq("creates equipment", CompanyOne.admin, 201, Method.CREATE, Json.encodeToString(createCmd)),
            ParameterizedReq("creates equipment", CompanyOne.owner, 201, Method.CREATE, Json.encodeToString(createCmd)),
            ParameterizedReq(
                "creates equipment",
                CompanyOne.stageHand,
                403,
                Method.CREATE,
                Json.encodeToString(createCmd)
            ),

            // READ
            ParameterizedReq("reads equipment", CompanyOne.admin, 200, Method.READ, equipment.id.toString()),
            ParameterizedReq("reads equipment", CompanyOne.owner, 200, Method.READ, equipment.id.toString()),
            ParameterizedReq("reads equipment", CompanyOne.worker, 200, Method.READ, equipment.id.toString()),

            // READ ALL
            ParameterizedReq("reads all equipment", CompanyOne.admin, 200, Method.READ, null),
            ParameterizedReq("reads all equipment", CompanyOne.owner, 200, Method.READ, null),
            ParameterizedReq("reads all equipment", CompanyOne.worker, 200, Method.READ, null),

            // UPDATE
            ParameterizedReq("update equipment", CompanyOne.admin, 200, Method.UPDATE, Json.encodeToString(updateCmd)),
            ParameterizedReq("update equipment", CompanyOne.owner, 200, Method.UPDATE, Json.encodeToString(updateCmd)),
            ParameterizedReq(
                "update equipment",
                CompanyOne.worker.copy(role = UserRole.StageHand),
                200,
                Method.UPDATE,
                Json.encodeToString(updateCmd)
            ),

            // DELETE
            ParameterizedReq("delete equipment", CompanyOne.admin, 204, Method.DELETE, equipment.id.toString()),
            ParameterizedReq("delete equipment", CompanyOne.owner, 204, Method.DELETE, equipment.id.toString()),
            ParameterizedReq(
                "delete equipment",
                CompanyOne.stageHand,
                403,
                Method.DELETE,
                equipment.id.toString()
            ),
        )
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("requestMatrix")
    override fun makeRequest(pr: ParameterizedReq) = configuredTestApplication {
        dependencies.provide<EquipmentRepository> { equipmentRepository }
        dependencies.provide<UserRepository> { usersRepository }
        dependencies.provide<WarehouseRepository> { warehouseRepository }
        dependencies.provide<EquipmentStorageRepository> { equipmentStorageRepository }
        dependencies.provide<LoggableRepository> { loggableRepository }
        dependencies.provide<CompanyRepository> { companyRepository }
        every { equipmentRepository.allEquipment() } returns listOf(equipment)
        every { equipmentRepository.addEquipment(any()) } returns equipment
        every { equipmentRepository.updateEquipment(equipment.id, updateCmd) } returns equipment
        every { equipmentRepository.removeEquipment(equipment.id) } returns equipment
        every { equipmentRepository.equipmentById(equipment.id) } returns equipment
        every { warehouseRepository.warehouseById(createCmd.belongsToWarehouse) } returns warehouse
        every { equipmentStorageRepository.addEquipmentStorage(any()) } returns equipmentStorage
        every { loggableRepository.loggableById(loggable.id) } returns loggable
        every { equipmentStorageRepository.equipmentStorageById(equipmentStorage.id) } returns equipmentStorage
        every {
            equipmentStorageRepository.updateEquipmentStorage(
                equipmentStorage.id,
                updateStorageCmd
            )
        } returns equipmentStorage
        every { loggableRepository.updateLoggable(loggable.id, updateLoggableCmd) } returns loggable
        mockAuthRelatedMethods()

        client.webSocket("/ws") {
            run {
                sendLoginFrame(this@configuredTestApplication, pr.user)
                sendAndAssert("equipments", pr.method, pr.payload, pr.expectedStatus)
            }
        }
    }
}