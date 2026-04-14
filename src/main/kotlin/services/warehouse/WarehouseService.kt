package at.eventful.messless.services.warehouse

import at.eventful.messless.errors.responses.Forbidden
import at.eventful.messless.errors.responses.NotFound
import at.eventful.messless.errors.responses.Unauthorized
import at.eventful.messless.plugins.socket.ServiceMethod
import at.eventful.messless.plugins.socket.WebSocketService
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import at.eventful.messless.repositories.loggable.LoggableRepository
import at.eventful.messless.repositories.warehouse.WarehouseRepository
import at.eventful.messless.schema.dto.WarehouseDto
import at.eventful.messless.schema.utils.UserRole
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import repositories.warehouse.command.CreateWarehouseCmd
import repositories.warehouse.command.UpdateWarehouseCmd

class WarehouseService(app: Application) : WebSocketService("warehouse") {
    val warehouseRepository: WarehouseRepository by app.dependencies
    val loggableRepository: LoggableRepository by app.dependencies
    override fun ServiceMethod.create(): WebSocketResponse<WarehouseDto> {
        connection.auth.auth?.let {
            if (it.user.role.asInt() < UserRole.Manager.asInt()) throw Forbidden("You are not allowed to create a warehouse!")
            val cmd = incoming.receiveBody<CreateWarehouseCmd>()
            if (it.user.role.asInt() != UserRole.Admin.asInt() && cmd.companyId != it.user.company?.id) throw Forbidden(
                "You are not allowed to create a warehouse in this company!"
            )
            try {
                val warehouse = warehouseRepository.addWarehouse(cmd)
                return WebSocketResponse.from(
                    HttpStatusCode.Created,
                    WarehouseDto.from(
                        warehouse,
                        loggableRepository.loggableById(
                            warehouse.loggable?.id ?: throw NotFound("Event ${warehouse.loggable?.id} not found!")
                        ) ?: throw NotFound("Event ${warehouse.loggable?.id} not found!")
                    )
                )
            } catch (e: Exception) {
                throw e
            }
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.find(): WebSocketResponse<List<WarehouseDto>> {
        connection.auth.auth?.let { auth ->
            val companyId = auth.user.company?.id
            return WebSocketResponse.from(
                HttpStatusCode.OK,
                warehouseRepository.allWarehouses().filter { it.company?.id == companyId }
                    .map { (warehouse, _, loggable) ->
                        WarehouseDto.from(
                            warehouseRepository.warehouseById(warehouse)!!,
                            loggable!!
                        )
                    }
            )
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.get(id: Int): WebSocketResponse<WarehouseDto> {
        connection.auth.auth?.let {
            val warehouse = warehouseRepository.warehouseById(id) ?: throw NotFound("Warehouse with id $id not found")
            if (warehouse.company?.id != it.user.company?.id) throw Forbidden("You are not allowed to access this warehouse!")
            return WebSocketResponse.from(
                HttpStatusCode.OK,
                WarehouseDto.from(
                    warehouse,
                    loggableRepository.loggableById(
                        warehouse.loggable?.id ?: throw NotFound("Event ${warehouse.loggable?.id} not found!")
                    ) ?: throw NotFound("Event ${warehouse.loggable?.id} not found!")
                )
            )
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.update(id: Int): WebSocketResponse<WarehouseDto> {
        connection.auth.auth?.let {
            if (it.user.role.asInt() < 3) throw Forbidden("You are not allowed to update a warehouse!")
            val cmd = incoming.receiveBody<UpdateWarehouseCmd>()
            if (it.user.role.asInt() != UserRole.Admin.asInt() && cmd.companyId != it.user.company?.id) throw Forbidden(
                "You are not allowed to create a warehouse in this company!"
            )
            val warehouse =
                warehouseRepository.updateWarehouse(id, cmd) ?: throw NotFound("Warehouse with id $id not found")
            return WebSocketResponse.from(
                HttpStatusCode.OK,
                WarehouseDto.from(
                    warehouse,
                    loggableRepository.loggableById(
                        warehouse.loggable?.id ?: throw NotFound("Event ${warehouse.loggable?.id} not found!")
                    ) ?: throw NotFound("Event ${warehouse.loggable?.id} not found!")
                )
            )
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.delete(id: Int): WebSocketResponse<Nothing> {
        connection.auth.auth?.let {
            if (it.user.role.asInt() < 3) throw Forbidden("You are not allowed to delete a warehouse!")
            if (it.user.company?.id != warehouseRepository.warehouseById(id)?.company?.id) throw Forbidden("You are not allowed to delete this warehouse!")
            warehouseRepository.removeWarehouse(id) ?: throw NotFound("Warehouse with id $id not found")
            return WebSocketResponse(
                HttpStatusCode.NoContent
            )
        }
        throw Unauthorized()
    }
}