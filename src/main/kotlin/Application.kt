package at.eventful.messless

import at.eventful.messless.plugins.socket.WebSocketRouter
import at.eventful.messless.plugins.socket.configureWebSocket
import at.eventful.messless.services.echo.EchoService
import at.eventful.messless.services.index.registerIndexRoute
import at.eventful.messless.services.users.UsersService
import io.ktor.server.application.*

val router = WebSocketRouter()

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    // Install plugins
    configureWebSocket()

    // Register HTTP routes
    registerIndexRoute()

    // Register WS routes
    router.removeAllRoutes()
    router.register(EchoService())
    router.register(UsersService())
}