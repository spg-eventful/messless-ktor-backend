package at.eventful.messless.plugins.socket

import at.eventful.messless.plugins.socket.model.IncomingMessage
import at.eventful.messless.plugins.socket.model.WebSocketConnection
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import io.ktor.util.logging.*

internal val LOGGER = KtorSimpleLogger("WebSocketRouter")

class WebSocketRouter {
    private val routes = LinkedHashMap<String, WebSocketService>()

    fun register(vararg services: WebSocketService) = services.forEach { register(it) }
    fun register(service: WebSocketService) {
        LOGGER.info("Registering service ${service.name}")
        if (routes.containsKey(service.name.lowercase())) TODO("Handle service already registered")
        routes[service.name.lowercase()] = service
    }


    fun route(
        incoming: IncomingMessage,
        connection: WebSocketConnection
    ): WebSocketResponse {
        LOGGER.trace("Routing ${incoming.method.name} ${incoming.service}")
        val service = routes[incoming.service] ?: TODO("THROW NOTFOUND")
        return service.route(incoming, connection)
    }
}