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
import at.eventful.messless.schema.dto.EventDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*

class EventsService(app: Application) : WebSocketService("events") {
    val eventsRepo: EventRepository by app.dependencies

    override fun ServiceMethod.create(): WebSocketResponse<EventDto> {
        connection.auth.auth?.let {
            if (it.user.role.asInt() > 2) throw Forbidden("You are not allowed to create events!")

            val cmd = incoming.receiveBody<CreateEventCmd>()

            try {
                return WebSocketResponse.from(
                    HttpStatusCode.Created,
                    EventDto.from(eventsRepo.addEvent(cmd)),
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
                eventsRepo.allEvents().map(EventDto::from),
            )
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.get(id: Int): WebSocketResponse<EventDto> {
        connection.auth.auth?.let {
            val event = eventsRepo.eventById(id) ?: throw NotFound("Event with id $id not found")
            return WebSocketResponse.from(
                HttpStatusCode.OK,
                EventDto.from(event),
            )
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.update(id: Int): WebSocketResponse<EventDto> {
        connection.auth.auth?.let {
            val updated = eventsRepo.updateEvent(id, incoming.receiveBody<UpdateEventCmd>())
                ?: throw NotFound("Event with id $id not found")
            return WebSocketResponse.from(
                HttpStatusCode.OK,
                EventDto.from(updated),
            )
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.delete(id: Int): WebSocketResponse<EventDto> {
        connection.auth.auth?.let {
            if (it.user.role.asInt() > 2) throw Forbidden("You are not allowed to delete events!")
            eventsRepo.removeEvent(id) ?: throw NotFound("Event with id $id not found")
            return WebSocketResponse(
                HttpStatusCode.NoContent
            )
        }
        throw Unauthorized()
    }
}