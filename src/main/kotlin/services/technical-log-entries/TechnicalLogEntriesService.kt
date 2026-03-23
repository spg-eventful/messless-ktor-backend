package at.eventful.messless.services.technicalLogEntries

import at.eventful.messless.errors.responses.Forbidden
import at.eventful.messless.errors.responses.Unauthorized
import at.eventful.messless.plugins.socket.ServiceMethod
import at.eventful.messless.plugins.socket.WebSocketService
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import at.eventful.messless.repositories.technicalLogEntries.TechnicalLogEntryRepository
import at.eventful.messless.repositories.technicalLogEntries.commands.CreateTechnicalLogEntryCmd
import at.eventful.messless.repositories.technicalLogEntries.commands.FindTechnicalLogByEquipmentCmd
import at.eventful.messless.schema.dto.TechnicalLogEntryDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import org.jetbrains.exposed.v1.core.exposedLogger

//TODO: check for company
class TechnicalLogEntriesService(app: Application) : WebSocketService("technical-log-entries") {
    val technicalLogEntryRepo: TechnicalLogEntryRepository by app.dependencies

    override fun ServiceMethod.create(): WebSocketResponse<TechnicalLogEntryDto> {
        connection.auth.auth?.let {
            val cmd = incoming.receiveBody<CreateTechnicalLogEntryCmd>()
            try {
                return WebSocketResponse.from(
                    HttpStatusCode.Created,
                    TechnicalLogEntryDto.from(technicalLogEntryRepo.addTechnicalLogEntry(cmd))
                )
            } catch ( e: Exception ){
                throw e
            }
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.find(): WebSocketResponse<List<TechnicalLogEntryDto>> {
        val data = incoming.receiveBody<FindTechnicalLogByEquipmentCmd>()
        exposedLogger.info("FindTechnicalLogByEquipment: $data")
        connection.auth.auth?.let {
            return WebSocketResponse.from(
                HttpStatusCode.OK,
                technicalLogEntryRepo.allTechnicalLogEntries().map(TechnicalLogEntryDto::from)
            )
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.delete(id: Int): WebSocketResponse<Nothing> {
        connection.auth.auth?.let {
            technicalLogEntryRepo.removeTechnicalLogEntry(id)
            return WebSocketResponse(
                HttpStatusCode.NoContent
            )
        }
        throw Unauthorized()
    }
}