package at.eventful.messless.services.technicalLogEntries

import at.eventful.messless.errors.responses.Forbidden
import at.eventful.messless.errors.responses.NotFound
import at.eventful.messless.errors.responses.Unauthorized
import at.eventful.messless.plugins.socket.ServiceMethod
import at.eventful.messless.plugins.socket.WebSocketService
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import at.eventful.messless.repositories.equipment.EquipmentRepository
import at.eventful.messless.repositories.loggable.LoggableRepository
import at.eventful.messless.repositories.technicalLogEntries.TechnicalLogEntryRepository
import at.eventful.messless.repositories.technicalLogEntries.commands.CreateTechnicalLogEntryCmd
import at.eventful.messless.repositories.technicalLogEntries.commands.FindTechnicalLogByEquipmentCmd
import at.eventful.messless.repositories.warehouse.WarehouseRepository
import at.eventful.messless.schema.dto.TechnicalLogEntryDto
import at.eventful.messless.schema.utils.UserRole
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import org.jetbrains.exposed.v1.core.exposedLogger

//TODO: check for company
class TechnicalLogEntriesService(app: Application) : WebSocketService("technical-log-entries") {
    val technicalLogEntryRepo: TechnicalLogEntryRepository by app.dependencies
    val equipmentRepo: EquipmentRepository by app.dependencies
    val warehouseRepo: WarehouseRepository by app.dependencies
    val loggableRepo: LoggableRepository by app.dependencies

    override fun ServiceMethod.create(): WebSocketResponse<TechnicalLogEntryDto> {
        connection.auth.auth?.let {
            val cmd = incoming.receiveBody<CreateTechnicalLogEntryCmd>()
            if (it.user.company?.id != getCompanyId(cmd.attachedTo) && it.user.role.asInt() != UserRole.Admin.asInt()) throw Forbidden(
                "You are not allowed to create a technical log entry in this company!"
            )
            try {
                val technicalLog = technicalLogEntryRepo.addTechnicalLogEntry(cmd, it.user.id)
                return WebSocketResponse.from(
                    HttpStatusCode.Created,
                    TechnicalLogEntryDto.from(
                        technicalLog,
                        loggableRepo.loggableById(
                            technicalLog.loggable?.id ?: throw NotFound("Loggable ${technicalLog.loggable?.id} not found!")
                        ) ?: throw NotFound("Loggable ${technicalLog.loggable.id} not found!")
                    )
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
            val userCompanyId = it.user.company?.id ?: throw Forbidden("User has no company assigned")
            val entries = if (data != null) {
                if (userCompanyId != getCompanyId(data.equipmentId)) {
                    throw Forbidden(
                        "You are not allowed to access this company's technical log entries!"
                    )
                }
                technicalLogEntryRepo.allTechnicalLogEntries().filter { it.attachedTo?.id == data.equipmentId }

            } else technicalLogEntryRepo.allTechnicalLogEntries()

            return WebSocketResponse.from(
                HttpStatusCode.OK,
                entries.map { (technicalLog, _, _, _, loggable) ->
                    TechnicalLogEntryDto.from(
                        technicalLogEntryRepo.technicalLogEntryById(technicalLog)
                            ?: throw NotFound("TechnicalLog with id ${technicalLog} not found"),
                        loggable ?: throw NotFound("Loggable with id ${loggable} not found")
                    )
                }.filter { getCompanyId(it.attachedTo) == userCompanyId }
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