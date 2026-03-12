package at.eventful.messless.services.eqipments

import at.eventful.messless.errors.responses.NotFound
import at.eventful.messless.plugins.socket.ServiceMethod
import at.eventful.messless.plugins.socket.WebSocketService
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import at.eventful.messless.repositories.equipment.EquipmentRepository
import at.eventful.messless.repositories.equipment.commands.CreateEquipmentCmd
import at.eventful.messless.repositories.equipment.commands.UpdateEquipmentCmd
import at.eventful.messless.schema.dto.EquipmentDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*

class EquipmentService(app: Application) : WebSocketService("equipment") {
    val equipmentRepo: EquipmentRepository by app.dependencies

    override fun ServiceMethod.create(): WebSocketResponse<EquipmentDto> {
        val cmd = incoming.receiveBody<CreateEquipmentCmd>()

        try {
            return WebSocketResponse.from(
                HttpStatusCode.Created,
                EquipmentDto.from(equipmentRepo.addEquipment(cmd)),
            )
        } catch (e: Exception) {
            throw e
        }
    }

    override fun ServiceMethod.find(): WebSocketResponse<List<EquipmentDto>> {
        return WebSocketResponse.from(
            HttpStatusCode.OK,
            equipmentRepo.allEquipment().map(EquipmentDto::from),
        )
    }

    override fun ServiceMethod.get(id: Int): WebSocketResponse<EquipmentDto> {
        val eqipment = equipmentRepo.equipmentById(id) ?: throw NotFound("Equipment with id $id not found")
        return WebSocketResponse.from(
            HttpStatusCode.OK,
            EquipmentDto.from(eqipment),
        )
    }

    override fun ServiceMethod.update(id: Int): WebSocketResponse<EquipmentDto> {
        val updated = equipmentRepo.updateEquipment(id, incoming.receiveBody<UpdateEquipmentCmd>())
            ?: throw NotFound("Equipment with id $id not found")
        return WebSocketResponse.from(
            HttpStatusCode.OK,
            EquipmentDto.from(updated),
        )
    }

    override fun ServiceMethod.delete(id: Int): WebSocketResponse<EquipmentDto> {
        equipmentRepo.removeEquipment(id) ?: throw NotFound("Equipment with id $id not found")
        return WebSocketResponse(
            HttpStatusCode.NoContent
        )
    }
}