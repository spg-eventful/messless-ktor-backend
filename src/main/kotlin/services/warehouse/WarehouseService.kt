package at.eventful.messless.services.warehouse

import at.eventful.messless.errors.responses.BadRequest
import at.eventful.messless.errors.responses.NotFound
import at.eventful.messless.plugins.socket.ServiceMethod
import at.eventful.messless.plugins.socket.WebSocketService
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import at.eventful.messless.repositories.warehouse.WarehouseRepository
import at.eventful.messless.schema.dto.WarehouseDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import repositories.warehouse.command.CreateWarehouseCmd
import repositories.warehouse.command.UpdateWarehouseCmd

class WarehouseService(app: Application) : WebSocketService("warehouse") {
    val warehouseRepository: WarehouseRepository by app.dependencies

    override fun ServiceMethod.create(): WebSocketResponse<WarehouseDto> {
        val cmd = incoming.receiveBody<CreateWarehouseCmd>()
        try {
            return WebSocketResponse.from(
                HttpStatusCode.Created,
                WarehouseDto.from(warehouseRepository.addWarehouse(cmd))
            )
        } catch (e: Exception) {
            if (e.message?.contains("PUBLIC.USERS_EMAIL_UNIQUE") ?: false) {
                throw BadRequest("A user with this email already exists.")
            }
            throw e
        }
    }

    override fun ServiceMethod.find(): WebSocketResponse<List<WarehouseDto>> {
        return WebSocketResponse.from(
            HttpStatusCode.OK,
            warehouseRepository.allWarehouses().map(WarehouseDto::from)
        )
    }

    override fun ServiceMethod.get(id: Int): WebSocketResponse<WarehouseDto> {
        val warehouse = warehouseRepository.warehouseById(id) ?: throw NotFound("Warehouse with id $id not found")
        return WebSocketResponse.from(
            HttpStatusCode.OK,
            WarehouseDto.from(warehouse)
        )
    }

    override fun ServiceMethod.update(id: Int): WebSocketResponse<WarehouseDto> {
        val warehouse = warehouseRepository.updateWarehouse(id, incoming.receiveBody<UpdateWarehouseCmd>())
            ?: throw NotFound("Warehouse with id $id not found")
        return WebSocketResponse.from(
            HttpStatusCode.OK,
            WarehouseDto.from(warehouse)
        )
    }

    override fun ServiceMethod.delete(id: Int): WebSocketResponse<Nothing> {
        warehouseRepository.removeWarehouse(id) ?: throw NotFound("Warehouse with id $id not found")
        return WebSocketResponse(
            HttpStatusCode.NoContent
        )
    }
}