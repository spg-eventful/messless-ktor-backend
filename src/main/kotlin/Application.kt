package at.eventful.messless

import at.eventful.messless.plugins.socket.WebSocketRouter
import at.eventful.messless.plugins.socket.configureWebSocket
import at.eventful.messless.services.echo.EchoService
import at.eventful.messless.services.index.registerIndexRoute
import io.ktor.server.application.*
import org.jetbrains.exposed.v1.jdbc.Database

val router = WebSocketRouter()

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    Database.connect("jdbc:h2:mem:messlessBackendTest", driver = "org.h2.Driver")

    // Install plugins
    configureWebSocket()

    // Register HTTP routes
    registerIndexRoute()

    // Register WS routes
    router.removeAllRoutes()
    router.register(EchoService())
}