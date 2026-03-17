package at.eventful.messless.services.eqipments

import at.eventful.messless.errors.responses.Forbidden
import at.eventful.messless.errors.responses.NotFound
import at.eventful.messless.errors.responses.Unauthorized
import at.eventful.messless.plugins.socket.ServiceMethod
import at.eventful.messless.plugins.socket.WebSocketService
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import at.eventful.messless.repositories.equipment.EquipmentRepository
import at.eventful.messless.repositories.equipment.commands.CreateEquipmentCmd
import at.eventful.messless.repositories.equipment.commands.UpdateEquipmentCmd
import at.eventful.messless.schema.dto.EquipmentDto
import at.eventful.messless.schema.entities.WarehouseEntity
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*

class EquipmentsService(app: Application) : WebSocketService("equipments") {
    val equipmentRepo: EquipmentRepository by app.dependencies

    override fun ServiceMethod.create(): WebSocketResponse<EquipmentDto> {
        connection.auth.auth?.let {
            if (it.user.role.asInt() < 2) throw Forbidden("You are not allowed to create equipment!")

            val cmd = incoming.receiveBody<CreateEquipmentCmd>()
            val warehouse = WarehouseEntity.findById(cmd.belongsToWarehouse)

            if (warehouse?.company?.id?.value != it.user.company?.id) throw Forbidden("You are not allowed to create equipment in another companies warehouse!")

            try {
                return WebSocketResponse.from(
                    HttpStatusCode.Created,
                    EquipmentDto.from(equipmentRepo.addEquipment(cmd)),
                )
            } catch (e: Exception) {
                throw e
            }
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.find(): WebSocketResponse<List<EquipmentDto>> {
        connection.auth.auth?.let {
            return WebSocketResponse.from(
                HttpStatusCode.OK,
                equipmentRepo.allEquipment().map(EquipmentDto::from),
            )
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.get(id: Int): WebSocketResponse<EquipmentDto> {
        connection.auth.auth?.let {
            val equipment = equipmentRepo.equipmentById(id) ?: throw NotFound("Equipment with id $id not found")
            return WebSocketResponse.from(
                HttpStatusCode.OK,
                EquipmentDto.from(equipment),
            )
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.update(id: Int): WebSocketResponse<EquipmentDto> {
        connection.auth.auth?.let {
            val updated = equipmentRepo.updateEquipment(id, incoming.receiveBody<UpdateEquipmentCmd>())
                ?: throw NotFound("Equipment with id $id not found")
            return WebSocketResponse.from(
                HttpStatusCode.OK,
                EquipmentDto.from(updated),
            )
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.delete(id: Int): WebSocketResponse<EquipmentDto> {
        connection.auth.auth?.let {
            if (it.user.role.asInt() < 2) throw Forbidden("You are not allowed to delete equipment!")
            equipmentRepo.removeEquipment(id) ?: throw NotFound("Equipment with id $id not found")
            return WebSocketResponse(
                HttpStatusCode.NoContent
            )
        }
        throw Unauthorized()
    }
}