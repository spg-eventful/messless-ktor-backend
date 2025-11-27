package at.eventful.messless.plugins.socket

import at.eventful.messless.plugins.socket.model.IncomingMessage
import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.plugins.socket.model.WebSocketConnection
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import io.ktor.http.*

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
    private fun methodNotAllowed(): WebSocketResponse {
        return WebSocketResponse(
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
    ): WebSocketResponse {
        val serviceMethod = ServiceMethod(incoming, connection)

        return when (incoming.method) {
            Method.CREATE -> serviceMethod.create()
            Method.READ -> {
                val id = incoming.body?.toIntOrNull() ?: return serviceMethod.find()
                serviceMethod.get(id)
            }

            Method.UPDATE -> serviceMethod.update()
            Method.DELETE -> serviceMethod.delete()
        }
    }

    /** Create/Insert a new entity */
    open fun ServiceMethod.create(): WebSocketResponse {
        return methodNotAllowed()
    }

    /** Get all entities */
    open fun ServiceMethod.find(): WebSocketResponse {
        return methodNotAllowed()
    }

    /** Get a single entity, with id */
    open fun ServiceMethod.get(id: Int): WebSocketResponse {
        return methodNotAllowed()
    }

    /** Update an existing entity partially */
    open fun ServiceMethod.update(): WebSocketResponse {
        return methodNotAllowed()
    }

    /** Delete an entity */
    open fun ServiceMethod.delete(): WebSocketResponse {
        return methodNotAllowed()
    }
}