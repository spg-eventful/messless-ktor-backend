package services.warehouses

import at.eventful.messless.plugins.socket.model.IncomingMessage
import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import at.eventful.messless.repositories.warehouse.WarehouseRepository
import at.eventful.messless.repositories.warehouse.WarehouseRepositoryImpl
import at.eventful.messless.schema.dao.WarehouseDao
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.extension.ExtendWith
import repositories.warehouse.command.CreateWarehouseCmd
import repositories.warehouse.command.UpdateWarehouseCmd
import testutils.configuredTestApplication
import testutils.receiveText
import kotlin.test.Test
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class WarehouseServiceTest {
    val warehouseRepository = mockk<WarehouseRepository>()

    fun warehouseFakeCreateCmd(): CreateWarehouseCmd = CreateWarehouseCmd(
        "Test Warehouse", 1.0, 1.0
    )

    @Test
    fun testWarehouseCreation() = configuredTestApplication {
        dependencies.provide<WarehouseRepository> {
            warehouseRepository
        }

        every { warehouseRepository.addWarehouse(any()) } returns WarehouseDao.fake(1)

        client.webSocket("/ws") {
            run {
                send(
                    Frame.Text(
                        IncomingMessage(
                            0, "warehouse", Method.CREATE, Json.encodeToString(warehouseFakeCreateCmd())
                        ).toString()
                    )
                )
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(201, res.statusCode)
            }
        }
    }

    @Test
    fun testWarehouseNotFound() = configuredTestApplication {
        client.webSocket("/ws") {
            run {
                send(
                    Frame.Text(
                        IncomingMessage(
                            0, "warehouse", Method.READ, "1"
                        ).toString()
                    )
                )
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(404, res.statusCode)
            }
        }
    }

    @Test
    fun testWarehouseRead() = configuredTestApplication {
        dependencies.provide<WarehouseRepository> {
            warehouseRepository
        }

        every { warehouseRepository.warehouseById(1) } returns WarehouseDao.fake(1)

        client.webSocket("/ws") {
            run {
                send(
                    Frame.Text(
                        IncomingMessage(
                            0, "warehouse", Method.READ, "1"
                        ).toString()
                    )
                )
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(200, res.statusCode)
            }
        }
    }

    @Test
    fun testWarehouseReadAll() = configuredTestApplication {
        client.webSocket("/ws") {
            run {
                send(
                    Frame.Text(
                        IncomingMessage(
                            0, "warehouse", Method.READ
                        ).toString()
                    )
                )
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(200, res.statusCode)
            }
        }
    }

    @Test
    fun testWarehouseUpdate() = configuredTestApplication {
        val fakeWarehouse = WarehouseDao.fake(1)
        val cmd = UpdateWarehouseCmd(
            fakeWarehouse.id,
            fakeWarehouse.label,
            fakeWarehouse.latitude,
            fakeWarehouse.longitude,
        )

        dependencies.provide<WarehouseRepository> {
            warehouseRepository
        }
        every { warehouseRepository.updateWarehouse(1, cmd) } returns fakeWarehouse

        client.webSocket("/ws") {
            run {
                send(
                    Frame.Text(
                        IncomingMessage(
                            0, "warehouse", Method.UPDATE, Json.encodeToString(cmd)
                        ).toString()
                    )
                )
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(200, res.statusCode)
            }
        }
    }

    @Test
    fun testWarehouseUpdateNotFound() = configuredTestApplication {
        val fakeWarehouse = WarehouseDao.fake(1)
        val cmd = UpdateWarehouseCmd(
            2,
            fakeWarehouse.label,
            fakeWarehouse.latitude,
            fakeWarehouse.longitude,
        )

        dependencies.provide<WarehouseRepository> {
            warehouseRepository
        }
        every { warehouseRepository.updateWarehouse(2, cmd) } returns null

        client.webSocket("/ws") {
            run {
                send(
                    Frame.Text(
                        IncomingMessage(
                            0, "warehouse", Method.UPDATE, Json.encodeToString(cmd)
                        ).toString()
                    )
                )
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(404, res.statusCode)
            }
        }
    }

    @Test
    fun testDeleteWarehouse() = configuredTestApplication {
        val fakeWarehouse = WarehouseDao.fake(1)
        dependencies.provide<WarehouseRepository> {
            warehouseRepository
        }
        every { warehouseRepository.removeWarehouse(1) } returns fakeWarehouse

        client.webSocket("/ws") {
            run {
                send(
                    Frame.Text(
                        IncomingMessage(
                            0, "warehouse", Method.DELETE, "1"
                        ).toString()
                    )
                )
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(204, res.statusCode)
            }
        }
    }
}