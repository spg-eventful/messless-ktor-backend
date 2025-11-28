package at.eventful.messless.plugins.socket

import at.eventful.messless.errors.ServiceAlreadyRegistered
import at.eventful.messless.errors.WebSocketErrorResponse
import at.eventful.messless.plugins.socket.model.IncomingMessage
import at.eventful.messless.plugins.socket.model.WebSocketConnection
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import io.ktor.http.*
import io.ktor.util.logging.*

internal val LOGGER = KtorSimpleLogger("WebSocketRouter")

/**
 * Routes [IncomingMessage] through the router to the services.
 * A service has to register to the router (via [register]), before being able to receive requests.
 */
class WebSocketRouter {
    private val routes = LinkedHashMap<String, WebSocketService>()

    /** Remove all routes from this router */
    fun removeAllRoutes() = routes.clear()

    /** Let the router know the [services] exist. This has to be done for a service to be routable */
    fun register(vararg services: WebSocketService) = services.forEach { register(it) }

    /** Let the router know the [service] exists. This has to be done for a service to be routable */
    fun register(service: WebSocketService) {
        LOGGER.info("Registering service ${service.name}")
        if (routes.containsKey(service.name.lowercase())) {
            throw ServiceAlreadyRegistered(service)
        }

        routes[service.name.lowercase()] = service
    }


    /**
     * Route a request through the router to the correct service.
     */
    fun route(
        incoming: IncomingMessage,
        connection: WebSocketConnection
    ): WebSocketResponse {
        LOGGER.trace("Routing ${incoming.method.name} ${incoming.service}")
        val service = routes[incoming.service] ?: throw WebSocketErrorResponse(
            HttpStatusCode.NotFound, "${incoming.service} service not found!"
        )
        return service.route(incoming, connection)
    }
}