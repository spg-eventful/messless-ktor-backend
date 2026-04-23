package at.eventful.messless.services.events

import at.eventful.messless.errors.responses.Forbidden
import at.eventful.messless.errors.responses.NotFound
import at.eventful.messless.errors.responses.Unauthorized
import at.eventful.messless.plugins.socket.ServiceMethod
import at.eventful.messless.plugins.socket.WebSocketService
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import at.eventful.messless.repositories.event.EventRepository
import at.eventful.messless.repositories.event.commands.CreateEventCmd
import at.eventful.messless.repositories.event.commands.UpdateEventCmd
import at.eventful.messless.repositories.loggable.LoggableRepository
import at.eventful.messless.schema.dto.EventDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*

class EventsService(app: Application) : WebSocketService("events") {
    val eventsRepo: EventRepository by app.dependencies
    val loggableRepo: LoggableRepository by app.dependencies

    override fun ServiceMethod.create(): WebSocketResponse<EventDto> {
        connection.auth.auth?.let { auth ->
            if (auth.user.role.asInt() < 3) throw Forbidden("You are not allowed to create events!")

            val cmd = incoming.receiveBody<CreateEventCmd>()
            cmd.companyId = auth.user.company?.id ?: throw Forbidden("User has no company!")

            try {
                val addedEvent = eventsRepo.addEvent(cmd)
                val loggable = loggableRepo.loggableById(
                    addedEvent.loggable?.id ?: throw NotFound("Event ${addedEvent.loggable?.id} not found!")
                ) ?: throw NotFound("Event ${addedEvent.loggable?.id} not found!")
                return WebSocketResponse.from(
                    HttpStatusCode.Created,
                    EventDto.from(addedEvent, loggable)
                )
            } catch (e: Exception) {
                throw e
            }
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.find(): WebSocketResponse<List<EventDto>> {
        connection.auth.auth?.let {
            return WebSocketResponse.from(
                HttpStatusCode.OK,
                eventsRepo.allEvents()
                    .map { event -> EventDto.from(event, event.loggable!!) },
            )
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.get(id: Int): WebSocketResponse<EventDto> {
        connection.auth.auth?.let {
            val event = eventsRepo.eventById(id) ?: throw NotFound("Event with id $id not found")
            return WebSocketResponse.from(
                HttpStatusCode.OK,
                EventDto.from(
                    event,
                    loggableRepo.loggableById(
                        event.loggable?.id ?: throw NotFound("Event ${event.loggable?.id} not found!")
                    ) ?: throw NotFound("Event ${event.loggable?.id} not found!")
                ),
            )
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.update(id: Int): WebSocketResponse<EventDto> {
        connection.auth.auth?.let {
            if (it.user.role.asInt() < 3) throw Forbidden("You are not allowed to update events!")

            val updated = eventsRepo.updateEvent(id, incoming.receiveBody<UpdateEventCmd>())
                ?: throw NotFound("Event with id $id not found")
            return WebSocketResponse.from(
                HttpStatusCode.OK,
                EventDto.from(
                    updated,
                    loggableRepo.loggableById(
                        updated.loggable?.id ?: throw NotFound("Event ${updated.loggable?.id} not found!")
                    ) ?: throw NotFound("Event ${updated.loggable?.id} not found!")
                ),
            )
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.delete(id: Int): WebSocketResponse<EventDto> {
        connection.auth.auth?.let {
            if (it.user.role.asInt() < 3) throw Forbidden("You are not allowed to delete events!")
            eventsRepo.removeEvent(id) ?: throw NotFound("Event with id $id not found")
            return WebSocketResponse(
                HttpStatusCode.NoContent
            )
        }
        throw Unauthorized()
    }
}