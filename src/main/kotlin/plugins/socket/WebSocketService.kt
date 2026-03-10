package at.eventful.messless.plugins.socket

import at.eventful.messless.errors.WebSocketErrorResponse
import at.eventful.messless.errors.responses.BadRequest
import at.eventful.messless.plugins.socket.model.IncomingMessage
import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.plugins.socket.model.WebSocketConnection
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Holder of the high-level CRUD methods (create, find, get, update, delete).
 *
 * all high-level CRUD methods have read access to the [incoming] and [connection] objects.
 */
data class ServiceMethod(val incoming: IncomingMessage, val connection: WebSocketConnection)

/**
 * A service that accepts WebSocket requests and routes them to the respective service methods.
 *
 * The name of the service decides its path. A name can contain a slash to create sub-services.
 * (Example: `users`, `auth/register` or `auth/session`)
 */
open class WebSocketService(val name: String) {
    private val ignoreUnknownJson = Json { ignoreUnknownKeys = true }

    private fun methodNotAllowed(): WebSocketErrorResponse {
        return WebSocketErrorResponse(
            HttpStatusCode.MethodNotAllowed,
            "Method not implemented/allowed!"
        )
    }

    /**
     * After the [WebSocketRouter] routes the request to this service, the service routes the
     * request to its service methods.
     */
    fun route(
        incoming: IncomingMessage,
        connection: WebSocketConnection
    ): WebSocketResponse<*> {
        val serviceMethod = ServiceMethod(incoming, connection)
        val id = incoming.body?.toIntOrNull()

        return when (incoming.method) {
            Method.CREATE -> serviceMethod.create()
            Method.READ -> {
                serviceMethod.get(id ?: return serviceMethod.find())
            }

            Method.UPDATE -> {
                if (incoming.body == null) throw BadRequest("Update calls must include body")
                val updateId =
                    ignoreUnknownJson.parseToJsonElement(incoming.body).jsonObject[$$"$id"]?.jsonPrimitive?.int
                serviceMethod.update(
                    updateId
                        ?: throw BadRequest($$"Update calls must include id (of type Int) at body.$id json segment")
                )
            }

            Method.DELETE -> {
                serviceMethod.delete(id ?: throw BadRequest("Delete calls must include id"))
            }
        }
    }

    /** Create/Insert a new entity */
    open fun ServiceMethod.create(): WebSocketResponse<*> {
        throw methodNotAllowed()
    }

    /** Get all entities */
    open fun ServiceMethod.find(): WebSocketResponse<*> {
        throw methodNotAllowed()
    }

    /** Get a single entity, with id */
    open fun ServiceMethod.get(id: Int): WebSocketResponse<*> {
        throw methodNotAllowed()
    }

    /** Update an existing entity partially */
    open fun ServiceMethod.update(id: Int): WebSocketResponse<*> {
        throw methodNotAllowed()
    }

    /** Delete an entity by id */
    open fun ServiceMethod.delete(id: Int): WebSocketResponse<*> {
        throw methodNotAllowed()
    }
}