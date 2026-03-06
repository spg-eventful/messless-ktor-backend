package at.eventful.messless.services.warehouse

import at.eventful.messless.plugins.socket.ServiceMethod
import at.eventful.messless.plugins.socket.WebSocketService
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import at.eventful.messless.repositories.warehouse.WarehouseRepository
import io.ktor.http.HttpStatusCode
import repositories.warehouse.command.CreateWarehouseCmd
import repositories.warehouse.command.UpdateWarehouseCmd

class WarehouseService : WebSocketService("warehouse") {
    val warehouseRepository = WarehouseRepository()

    override fun ServiceMethod.create(): WebSocketResponse {
        val cmd = incoming.receiveBody<CreateWarehouseCmd>()
        return WebSocketResponse(
            HttpStatusCode.Created,
            warehouseRepository.addWarehouse(cmd).toExposedWarehouse().toString()
        )
    }

    override fun ServiceMethod.find(): WebSocketResponse {
        return WebSocketResponse(
            HttpStatusCode.OK,
            warehouseRepository.allWarehouses().stream().map { it.toExposedWarehouse() }.toString()
        )
    }

    override fun ServiceMethod.get(id: Int): WebSocketResponse {
        return WebSocketResponse(
            HttpStatusCode.OK,
            warehouseRepository.warehouseById(id)?.toExposedWarehouse().toString()
        )
    }

    override fun ServiceMethod.update(): WebSocketResponse {
        val cmd = incoming.receiveBody<UpdateWarehouseCmd>()
        return WebSocketResponse(
            HttpStatusCode.OK,
            warehouseRepository.updateWarehouse(cmd).toExposedWarehouse().toString()
        )
    }

    override fun ServiceMethod.delete(): WebSocketResponse {
        return WebSocketResponse(
            HttpStatusCode.NoContent,
            warehouseRepository.removeWarehouse(incoming.receiveBody<Int>()).toString()
        )
    }
}