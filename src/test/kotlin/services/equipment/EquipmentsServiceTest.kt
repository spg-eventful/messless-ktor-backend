package services.equipment

import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.repositories.equipment.EquipmentRepository
import at.eventful.messless.repositories.equipment.commands.CreateEquipmentCmd
import at.eventful.messless.repositories.equipment.commands.UpdateEquipmentCmd
import at.eventful.messless.schema.dao.EquipmentDao
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

    companion object : AuthorizationTestCompanion() {
        val equipment = EquipmentDao.fake(1)

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
                CompanyOne.worker.copy(role = UserRole.StageHand),
                403,
                Method.CREATE,
                Json.encodeToString(createCmd)
            ),
            ParameterizedReq(
                "creates equipment",
                CompanyOne.worker,
                403,
                Method.CREATE,
                Json.encodeToString(createCmd.copy(belongsToWarehouse = 2))
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
                CompanyOne.worker.copy(role = UserRole.StageHand),
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
        // TODO: Add WarehouseRepository
        every { equipmentRepository.allEquipment() } returns listOf(equipment)
        every { equipmentRepository.addEquipment(any()) } returns equipment
        every { equipmentRepository.updateEquipment(equipment.id, updateCmd) } returns equipment
        every { equipmentRepository.removeEquipment(equipment.id) } returns equipment
        every { equipmentRepository.equipmentById(equipment.id) } returns equipment
        // TODO: mock findById in WarehouseRepository
        mockAuthRelatedMethods()

        client.webSocket("/ws") {
            run {
                sendLoginFrame(this@configuredTestApplication, pr.user)
                sendAndAssert("equipments", pr.method, pr.payload, pr.expectedStatus)
            }
        }
    }


//    fun equipmentFakeCreateCmd(): CreateEquipmentCmd = CreateEquipmentCmd(
//        "Fender Champion II", 0.0, 0.0, 1, null
//    )
//
//    @Test
//    fun testEquipmentCreation() = configuredTestApplication {
//        dependencies.provide<EquipmentRepository> { equipmentRepository }
//        every { equipmentRepository.addEquipment(any()) } returns EquipmentDao.fake(1)
//
//        client.webSocket("/ws") {
//            run {
//                send(
//                    Frame.Text(
//                        IncomingMessage(
//                            0, "equipments", Method.CREATE, Json.encodeToString(
//                                equipmentFakeCreateCmd()
//                            )
//                        ).toString()
//                    )
//                )
//                val res = WebSocketResponse.fromString(receiveText())
//                assertEquals(201, res.statusCode)
//            }
//        }
//    }
//
//    @Test
//    fun testEquipmentNotFound() = configuredTestApplication {
//        client.webSocket("/ws") {
//            run {
//                send(
//                    Frame.Text(
//                        IncomingMessage(
//                            0, "equipments", Method.READ, "1"
//                        ).toString()
//                    )
//                )
//                val res = WebSocketResponse.fromString(receiveText())
//                assertEquals(404, res.statusCode)
//            }
//        }
//    }
//
//    @Test
//    fun testEquipmentRead() = configuredTestApplication {
//        dependencies.provide<EquipmentRepository> { equipmentRepository }
//        every { equipmentRepository.equipmentById(1) } returns EquipmentDao.fake(1)
//
//        client.webSocket("/ws") {
//            run {
//                send(
//                    Frame.Text(
//                        IncomingMessage(
//                            0, "equipments", Method.READ, "1"
//                        ).toString()
//                    )
//                )
//                val res = WebSocketResponse.fromString(receiveText())
//                assertEquals(200, res.statusCode)
//            }
//        }
//    }
//
//    @Test
//    fun testEquipmentReadAll() = configuredTestApplication {
//        client.webSocket("/ws") {
//            run {
//                send(
//                    Frame.Text(
//                        IncomingMessage(
//                            0, "equipments", Method.READ
//                        ).toString()
//                    )
//                )
//                val res = WebSocketResponse.fromString(receiveText())
//                assertEquals(200, res.statusCode)
//            }
//        }
//    }
//
//    @Test
//    fun testEquipmentUpdate() = configuredTestApplication {
//        val fakeEquipment = EquipmentDao.fake(1)
//        val cmd = UpdateEquipmentCmd(
//            fakeEquipment.id,
//            fakeEquipment.label,
//            fakeEquipment.longitude,
//            fakeEquipment.latitude,
//            fakeEquipment.belongsToWarehouse,
//            fakeEquipment.equipmentStorage,
//        )
//
//        dependencies.provide<EquipmentRepository> { equipmentRepository }
//        every { equipmentRepository.updateEquipment(1, any()) } returns fakeEquipment
//
//        client.webSocket("/ws") {
//            run {
//                send(
//                    Frame.Text(
//                        IncomingMessage(
//                            0, "equipments", Method.UPDATE, Json.encodeToString(cmd)
//                        ).toString()
//                    )
//                )
//                val res = WebSocketResponse.fromString(receiveText())
//                assertEquals(200, res.statusCode)
//            }
//        }
//    }
//
//    @Test
//    fun testEquipmentUpdateNotFound() = configuredTestApplication {
//        val fakeEquipment = EquipmentDao.fake(1)
//        val cmd = UpdateEquipmentCmd(
//            2,
//            fakeEquipment.label,
//            fakeEquipment.longitude,
//            fakeEquipment.latitude,
//            fakeEquipment.belongsToWarehouse,
//            fakeEquipment.equipmentStorage,
//        )
//
//        dependencies.provide<EquipmentRepository> { equipmentRepository }
//        every { equipmentRepository.updateEquipment(2, any()) } returns null
//
//        client.webSocket("/ws") {
//            run {
//                send(
//                    Frame.Text(
//                        IncomingMessage(
//                            0, "equipments", Method.UPDATE, Json.encodeToString(cmd)
//                        ).toString()
//                    )
//                )
//                val res = WebSocketResponse.fromString(receiveText())
//                assertEquals(404, res.statusCode)
//            }
//        }
//    }
//
//    @Test
//    fun testEquipmentDelete() = configuredTestApplication {
//        val fakeEquipment = EquipmentDao.fake(1)
//        dependencies.provide<EquipmentRepository> { equipmentRepository }
//        every { equipmentRepository.removeEquipment(1) } returns fakeEquipment
//
//        client.webSocket("/ws") {
//            run {
//                send(
//                    Frame.Text(
//                        IncomingMessage(
//                            0, "equipments", Method.DELETE, "1"
//                        ).toString()
//                    )
//                )
//                val res = WebSocketResponse.fromString(receiveText())
//                assertEquals(204, res.statusCode)
//            }
//        }
//    }
}