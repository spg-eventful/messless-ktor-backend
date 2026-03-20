package services.equipment

import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.repositories.equipment.EquipmentRepository
import at.eventful.messless.repositories.equipment.commands.CreateEquipmentCmd
import at.eventful.messless.repositories.equipment.commands.UpdateEquipmentCmd
import at.eventful.messless.repositories.warehouse.WarehouseRepository
import at.eventful.messless.schema.dao.EquipmentDao
import at.eventful.messless.schema.dao.WarehouseDao
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
import testutils.*

@ExtendWith(MockKExtension::class)
class EquipmentsServiceTest : AuthorizationTest() {
    val equipmentRepository = mockk<EquipmentRepository>()
    override val usersRepository = mockk<UserRepository>()
    val warehouseRepository = mockk<WarehouseRepository>()

    companion object : AuthorizationTestCompanion() {
        val equipment = EquipmentDao.fake(1)
        val warehouse = WarehouseDao.fake(1)

        val updateCmd = UpdateEquipmentCmd(
            equipment.id,
            equipment.label,
            equipment.longitude,
            equipment.latitude,
            equipment.belongsToWarehouse,
            equipment.equipmentStorage,
        )

        val createCmd = CreateEquipmentCmd(
            equipment.label,
            equipment.longitude,
            equipment.latitude,
            equipment.belongsToWarehouse,
            equipment.equipmentStorage,
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
        every { equipmentRepository.allEquipment() } returns listOf(equipment)
        every { equipmentRepository.addEquipment(any()) } returns equipment
        every { equipmentRepository.updateEquipment(equipment.id, updateCmd) } returns equipment
        every { equipmentRepository.removeEquipment(equipment.id) } returns equipment
        every { equipmentRepository.equipmentById(equipment.id) } returns equipment
        every { warehouseRepository.warehouseById(createCmd.belongsToWarehouse) } returns warehouse
        mockAuthRelatedMethods()

        client.webSocket("/ws") {
            run {
                sendLoginFrame(this@configuredTestApplication, pr.user)
                sendAndAssert("equipments", pr.method, pr.payload, pr.expectedStatus)
            }
        }
    }
}