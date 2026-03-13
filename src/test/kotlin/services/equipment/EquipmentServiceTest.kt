package services.equipment

import at.eventful.messless.plugins.socket.model.IncomingMessage
import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import at.eventful.messless.repositories.equipment.EquipmentRepository
import at.eventful.messless.repositories.equipment.commands.CreateEquipmentCmd
import at.eventful.messless.repositories.equipment.commands.UpdateEquipmentCmd
import at.eventful.messless.schema.dao.EquipmentDao
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.extension.ExtendWith
import testutils.configuredTestApplication
import testutils.receiveText
import kotlin.test.Test
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class EquipmentServiceTest {
    val equipmentRepository = mockk<EquipmentRepository>()

    fun equipmentFakeCreateCmd(): CreateEquipmentCmd = CreateEquipmentCmd(
        "Fender Champion II", 0.0, 0.0, 1, null
    )

    @Test
    fun testEquipmentCreation() = configuredTestApplication {
        dependencies.provide<EquipmentRepository> { equipmentRepository }
        every { equipmentRepository.addEquipment(any()) } returns EquipmentDao.fake(1)

        client.webSocket("/ws") {
            run {
                send(
                    Frame.Text(
                        IncomingMessage(
                            0, "equipments", Method.CREATE, Json.encodeToString(
                                equipmentFakeCreateCmd()
                            )
                        ).toString()
                    )
                )
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(201, res.statusCode)
            }
        }
    }

    @Test
    fun testEquipmentNotFound() = configuredTestApplication {
        client.webSocket("/ws") {
            run {
                send(
                    Frame.Text(
                        IncomingMessage(
                            0, "equipments", Method.READ, "1"
                        ).toString()
                    )
                )
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(404, res.statusCode)
            }
        }
    }

    @Test
    fun testEquipmentRead() = configuredTestApplication {
        dependencies.provide<EquipmentRepository> { equipmentRepository }
        every { equipmentRepository.equipmentById(1) } returns EquipmentDao.fake(1)

        client.webSocket("/ws") {
            run {
                send(
                    Frame.Text(
                        IncomingMessage(
                            0, "equipments", Method.READ, "1"
                        ).toString()
                    )
                )
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(200, res.statusCode)
            }
        }
    }

    @Test
    fun testEquipmentReadAll() = configuredTestApplication {
        client.webSocket("/ws") {
            run {
                send(
                    Frame.Text(
                        IncomingMessage(
                            0, "equipments", Method.READ
                        ).toString()
                    )
                )
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(200, res.statusCode)
            }
        }
    }

    @Test
    fun testEquipmentUpdate() = configuredTestApplication {
        val fakeEquipment = EquipmentDao.fake(1)
        val cmd = UpdateEquipmentCmd(
            fakeEquipment.id,
            fakeEquipment.label,
            fakeEquipment.longitude,
            fakeEquipment.latitude,
            fakeEquipment.belongsToWarehouse,
            fakeEquipment.equipmentStorage,
        )

        dependencies.provide<EquipmentRepository> { equipmentRepository }
        every { equipmentRepository.updateEquipment(1, any()) } returns fakeEquipment

        client.webSocket("/ws") {
            run {
                send(
                    Frame.Text(
                        IncomingMessage(
                            0, "equipments", Method.UPDATE, Json.encodeToString(cmd)
                        ).toString()
                    )
                )
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(200, res.statusCode)
            }
        }
    }

    @Test
    fun testEquipmentUpdateNotFound() = configuredTestApplication {
        val fakeEquipment = EquipmentDao.fake(1)
        val cmd = UpdateEquipmentCmd(
            2,
            fakeEquipment.label,
            fakeEquipment.longitude,
            fakeEquipment.latitude,
            fakeEquipment.belongsToWarehouse,
            fakeEquipment.equipmentStorage,
        )

        dependencies.provide<EquipmentRepository> { equipmentRepository }
        every { equipmentRepository.updateEquipment(2, any()) } returns null

        client.webSocket("/ws") {
            run {
                send(
                    Frame.Text(
                        IncomingMessage(
                            0, "equipments", Method.UPDATE, Json.encodeToString(cmd)
                        ).toString()
                    )
                )
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(404, res.statusCode)
            }
        }
    }

    @Test
    fun testEquipmentDelete() = configuredTestApplication {
        val fakeEquipment = EquipmentDao.fake(1)
        dependencies.provide<EquipmentRepository> { equipmentRepository }
        every { equipmentRepository.removeEquipment(1) } returns fakeEquipment

        client.webSocket("/ws") {
            run {
                send(
                    Frame.Text(
                        IncomingMessage(
                            0, "equipments", Method.DELETE, "1"
                        ).toString()
                    )
                )
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(204, res.statusCode)
            }
        }
    }
}