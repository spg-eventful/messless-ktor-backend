package at.eventful.messless.services.technicalLogEntries

import at.eventful.messless.errors.responses.Forbidden
import at.eventful.messless.errors.responses.Unauthorized
import at.eventful.messless.plugins.socket.ServiceMethod
import at.eventful.messless.plugins.socket.WebSocketService
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import at.eventful.messless.repositories.equipment.EquipmentRepositoryImpl
import at.eventful.messless.repositories.technicalLogEntries.TechnicalLogEntryRepository
import at.eventful.messless.repositories.technicalLogEntries.commands.CreateTechnicalLogEntryCmd
import at.eventful.messless.repositories.technicalLogEntries.commands.FindTechnicalLogByEquipmentCmd
import at.eventful.messless.repositories.warehouse.WarehouseRepositoryImpl
import at.eventful.messless.schema.dto.TechnicalLogEntryDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import org.jetbrains.exposed.v1.core.exposedLogger
import repositories.warehouse.command.CreateWarehouseCmd

//TODO: check for company
class TechnicalLogEntriesService(app: Application) : WebSocketService("technical-log-entries") {
    val technicalLogEntryRepo: TechnicalLogEntryRepository by app.dependencies
    val equipmentRepo: EquipmentRepositoryImpl by app.dependencies
    val warehouseRepo: WarehouseRepositoryImpl by app.dependencies

    override fun ServiceMethod.create(): WebSocketResponse<TechnicalLogEntryDto> {
        connection.auth.auth?.let {
            val cmd = incoming.receiveBody<CreateTechnicalLogEntryCmd>()
            if (it.user.company?.id != getCompanyId(cmd.attachedTo)) throw Forbidden(
                "You are not allowed to create a technical log entry in this company!"
            )
            try {
                return WebSocketResponse.from(
                    HttpStatusCode.Created,
                    TechnicalLogEntryDto.from(technicalLogEntryRepo.addTechnicalLogEntry(cmd))
                )
            } catch (e: Exception) {
                throw e
            }
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.find(): WebSocketResponse<List<TechnicalLogEntryDto>> {
        val data = incoming.receiveBody<FindTechnicalLogByEquipmentCmd>()
        exposedLogger.info("FindTechnicalLogByEquipment: $data")
        connection.auth.auth?.let {
            if (it.user.company?.id != getCompanyId(data.equipmentId)) throw Forbidden(
                "You are not allowed to access this company's technical log entries!"
            )
            return WebSocketResponse.from(
                HttpStatusCode.OK,
                technicalLogEntryRepo.allTechnicalLogEntries().map(TechnicalLogEntryDto::from)
            )
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.delete(id: Int): WebSocketResponse<Nothing> {
        connection.auth.auth?.let {
            if (it.user.company?.id != getCompanyId(id)) throw Forbidden(
                "You are not allowed to delete this company's technical log entries!"
            )
            technicalLogEntryRepo.removeTechnicalLogEntry(id)
            return WebSocketResponse(
                HttpStatusCode.NoContent
            )
        }
        throw Unauthorized()
    }

    fun getCompanyId(id: Int): Int? {
        return equipmentRepo.equipmentById(id)?.belongsToWarehouse?.let { warehouseRepo.warehouseById(it)?.company?.id }
    }
}